package com.example.android.expenses.ui.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android.expenses.R
import com.example.android.expenses.database.room.PaymentDB
import com.example.android.expenses.database.PaymentRepository
import com.example.android.expenses.databinding.ListFragmentBinding
import com.example.android.expenses.model.Payment
import com.example.android.expenses.ui.list.adapter.ListAdapter
import com.example.android.expenses.ui.list.adapter.ListListener
import com.example.android.expenses.ui.list.adapter.SwipeCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ListFragment : Fragment(), ListListener {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { PaymentDB.getInstance(requireActivity(), applicationScope) }
    private val repository by lazy { PaymentRepository(database.paymentDAO()) }

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!

    private var viewModel: ListViewModel? = null
    private val listAdapter = ListAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        val application = requireNotNull(this).activity?.application
        val viewModelFactory = application?.let { ListFactory(repository) }
        viewModel =
            viewModelFactory?.let { ViewModelProvider(this, it) }?.get(ListViewModel::class.java)

        viewModel?.payments?.observe(viewLifecycleOwner, {
            listAdapter.submitList(it)
        })

        binding.apply {
            recyclerView.adapter = listAdapter
            recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)

            ItemTouchHelper(object : SwipeCallback() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    val payment = viewModel?.payments?.value?.get(viewHolder.adapterPosition)
                    payment?.let { deleteItem(it) }
                }
            }).attachToRecyclerView(recyclerView)

            floatingActionButton.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_listFragment_to_addFragment)
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deleteItem(payment: Payment) {
        viewModel?.deletePayment(payment)
    }

}

