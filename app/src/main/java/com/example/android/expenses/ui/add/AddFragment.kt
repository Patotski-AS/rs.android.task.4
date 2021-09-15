package com.example.android.expenses.ui.add

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.android.expenses.R
import com.example.android.expenses.categories
import com.example.android.expenses.database.room.PaymentDB
import com.example.android.expenses.database.PaymentRepository
import com.example.android.expenses.databinding.AddFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AddFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database by lazy { PaymentDB.getInstance(requireActivity(), applicationScope) }
    private var _binding: AddFragmentBinding? = null
    private val binding get() = _binding!!
    private var viewModel: AddViewModel? = null

    private val payment by lazy { AddFragmentArgs.fromBundle(requireArguments()).payment }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddFragmentBinding.inflate(inflater, container, false)

        val application = requireNotNull(this).activity?.application
        val repository = application?.let { PaymentRepository(database.paymentDAO(), it) }

        val viewModelFactory = application?.let { repository?.let { it1 -> AddFactory(it1) } }
        viewModel =
            viewModelFactory?.let { ViewModelProvider(this, it) }?.get(AddViewModel::class.java)
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinner.adapter = spinnerAdapter
        binding.spinner.onItemSelectedListener = this

        binding.apply {
            if (payment != null) {
                editTextTextPersonName.setText(payment?.name.toString())
                editTextCosts.setText(payment?.cost.toString())
                editTextTextPersonName.hint = payment?.name.toString()
                editTextCosts.hint = payment?.cost.toString()
                spinner.setSelection(categories().indexOf(categories().find { it == payment?.category }))
                viewModel?.payment?.category = payment?.category
            }

            button.setOnClickListener {
                var name = ""
                var cost = ""
                try {
                    name = editTextTextPersonName.text.toString()
                    cost = editTextCosts.text.toString()
                    if (payment == null) {
                        viewModel?.payment?.name = name
                        viewModel?.payment?.cost = cost.toDouble()
                        viewModel?.addPayment()
                    } else {
                        payment?.name = name
                        payment?.cost = cost.toDouble()
                        payment?.category = viewModel?.payment?.category
                        payment?.let { viewModel?.updatePayment(it) }
                    }
                    view?.findNavController()?.navigate(R.id.action_addFragment_to_listFragment)

                } catch (e: Exception) {
                    if (name == "" || cost == "") {
                        Toast.makeText(requireContext(), "not all date entered", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "incorrect date entered",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            return binding.root
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val category = parent.getItemAtPosition(pos).toString()
        viewModel?.payment?.category = category
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

}