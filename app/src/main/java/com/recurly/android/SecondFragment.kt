package com.recurly.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.recurly.android.databinding.FragmentSecondBinding
import com.recurly.androidsdk.data.model.RecurlySessionData
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.network.core.RecurlyTokenizationHandler
import com.recurly.androidsdk.presentation.viewModel.RecurlyApi

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        RecurlySessionData.setPublicKey("ewr1-4TIXlPCkR68woNJp7UYMSL")

        binding.buttonTestSecond.setOnClickListener {
            val numberValidation =binding.recurlyCardNumber.validateData()
            val dateValidation =binding.recurlyExpirationDate.validateData()
            val cvvValidation = binding.recurlyCvvCode.validateData()
            if (numberValidation && dateValidation && cvvValidation) {
                binding.textviewSecond.text = "Loading..."
                val billingInfo = RecurlyApi.buildCreditCardBillingInfo(
                    "Hugo",
                    "Flores",
                    "CH2Solutions",
                    "home",
                    "",
                    "La Paz",
                    "La Paz",
                    "00000",
                    "Bolivia"
                )
                RecurlyApi.creditCardTokenization(
                    viewLifecycleOwner,
                    billingInfo,
                    object : RecurlyApi.ResponseHandler,
                        RecurlyTokenizationHandler {
                        override fun onSuccess(token: String, type: String) {
                            binding.textviewSecond.text = " token: $token \n type: $type"
                        }

                        override fun onError(error: ErrorRecurly) {
                            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                            binding.textviewSecond.text = gsonPretty.toJson(error)
                            error.fields.forEach {
                                if (it == "number"){
                                    binding.recurlyCardNumber.setCreditCardNumberError()
                                } else if (it == "year" || it == "month"){
                                    binding.recurlyExpirationDate.setExpirationError()
                                } else if (it == "cvv")
                                    binding.recurlyCvvCode.setCvvError()
                            }
                        }
                    })
            } else {
                var errorMessage = "Verify: \n"

                if (!numberValidation)
                    errorMessage = errorMessage + "Credit card number\n"
                if (!dateValidation)
                    errorMessage = errorMessage + "Expiration date\n"
                if (!cvvValidation)
                    errorMessage = errorMessage + "CVV code\n"

                binding.textviewSecond.text = errorMessage
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}