package com.example.android.expenses.ui.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import com.example.android.expenses.R
import com.example.android.expenses.categories
import com.example.android.expenses.database.room.PaymentDB
import com.example.android.expenses.database.PaymentRepository
import com.example.android.expenses.databinding.AddFragmentBinding
import com.example.android.expenses.model.Payment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AddFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { PaymentDB.getInstance(requireActivity(), applicationScope) }
    private val repository by lazy { PaymentRepository(database.paymentDAO()) }
    private var _binding: AddFragmentBinding? = null
    private val binding get() = _binding!!
    private var viewModel: AddViewModel? = null

    private var name : String? = null
    private var cost : Double? = null
    private var category : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddFragmentBinding.inflate(inflater, container, false)

        val application = requireNotNull(this).activity?.application
        val viewModelFactory = application?.let { AddFactory(repository) }
        viewModel =
            viewModelFactory?.let { ViewModelProvider(this, it) }?.get(AddViewModel::class.java)

        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinner.adapter = spinnerAdapter
        binding.spinner.onItemSelectedListener = this


        binding.apply {
            editTextTextPersonName.addTextChangedListener {
              name = it.toString()
            }

            editTextCosts.addTextChangedListener {
                cost = it.toString().toDouble()
            }

            button.setOnClickListener {
               viewModel?.addPayment(Payment(name, cost, category))
                view?.findNavController()?.navigate(R.id.action_addFragment_to_listFragment)
            }
        }

        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val category = parent.getItemAtPosition(pos).toString()
            this.category = category
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

}