package com.example.android.expenses.ui.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.expenses.R
import com.example.android.expenses.CURSOR
import com.example.android.expenses.PREF_LIST_MANAGEMENT
import com.example.android.expenses.database.room.PaymentDB
import com.example.android.expenses.database.PaymentRepository
import com.example.android.expenses.ROOM
import com.example.android.expenses.databinding.ListFragmentBinding
import com.example.android.expenses.model.Payment
import com.example.android.expenses.ui.list.adapter.ListAdapter
import com.example.android.expenses.ui.list.adapter.ListListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListFragment : Fragment(), ListListener {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { PaymentDB.getInstance(requireActivity(), applicationScope) }

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!

    private var viewModel: ListViewModel? = null
    private val listAdapter = ListAdapter(this)

    private lateinit var management: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        val application = requireNotNull(this).activity?.application
        val repository = application?.let { PaymentRepository(database.paymentDAO(), it) }
        val viewModelFactory =
            application?.let { repository?.let { repository -> ListFactory(repository) } }
        viewModel =
            viewModelFactory?.let { ViewModelProvider(this, it) }?.get(ListViewModel::class.java)

        val preferences = PreferenceManager.getDefaultSharedPreferences(application)
        management = preferences.getString(PREF_LIST_MANAGEMENT, ROOM).toString().trim()

        update()

        binding.apply {
            recyclerView.adapter = listAdapter
            recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)

//            ItemTouchHelper(object : SwipeCallback() {
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                    Log.i("MyLog", "viewHolder = ${viewHolder.adapterPosition}, direction = $direction  ")
//                    super.onSwiped(viewHolder, direction)
//                    deleteItem(viewHolder.layoutPosition)
//                    if (management == CURSOR) {
//                        viewModel?.updateCursorPayments()
//                    }
//                }
//            }).attachToRecyclerView(recyclerView)

            floatingActionButton.setOnClickListener {
                view?.findNavController()
                    ?.navigate(ListFragmentDirections.actionListFragmentToAddFragment(null))
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext(), R.style.AlertDialog).apply {
                setTitle("Quit the application?")
                setPositiveButton("Yes") { _, _ ->
                    ActivityCompat.finishAffinity(requireActivity())
                }
                setNegativeButton("No") { _, _ ->
                }
                setCancelable(true)
            }.create().show()
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
        if (management == CURSOR) {
            viewModel?.updateCursorPayments()
            lifecycle.coroutineScope.launch {
                viewModel?.cursorPayments?.collect {
                    listAdapter.submitList(it.toList())
                }
            }
        }
    }

    override fun onNodeLongClick(id: Int) {
        viewModel?.getPayment(id)
        lifecycle.coroutineScope.launch {
            view?.findNavController()
                ?.navigate(
                    ListFragmentDirections
                        .actionListFragmentToAddFragment(viewModel?.payment?.first())
                )
        }
    }

    private fun update(){
        if (management == ROOM) {
            lifecycle.coroutineScope.launch {
                viewModel?.payments?.collect {
                    listAdapter.submitList(it)
                }
            }
        } else {
            viewModel?.updateCursorPayments()
            lifecycle.coroutineScope.launch {
                viewModel?.cursorPayments?.collect {
                    listAdapter.submitList(it)
                }
                viewModel?.updateCursorPayments()
            }
        }
    }
}

