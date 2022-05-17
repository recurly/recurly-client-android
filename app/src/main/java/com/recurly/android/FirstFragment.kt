package com.recurly.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.recurly.android.databinding.FragmentFirstBinding
import com.recurly.androidsdk.data.model.RecurlySessionData
import com.recurly.androidsdk.data.model.tokenization.ErrorRecurly
import com.recurly.androidsdk.data.network.core.RecurlyTokenizationHandler
import com.recurly.androidsdk.presentation.viewModel.RecurlyApi
import com.recurly.androidsdk.presentation.viewModel.RecurlyApi.ResponseHandler

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        RecurlySessionData.setPublicKey("ewr1-4TIXlPCkR68woNJp7UYMSL")

        binding.testButton.setOnClickListener {
            val validationData = binding.recurlyUnifiedView.validateData()
            if (validationData.first && validationData.second && validationData.third) {
                binding.textApiText.text = "Loading..."
                val billingInfo = RecurlyApi.buildCreditCardBillingInfo(
                    "Hugo",
                    "Flores",
                    "CH2Solutions",
                    "Home",
                    "",
                    "La Paz",
                    "La Paz",
                    "00000",
                    "Bolivia"
                )
                RecurlyApi.creditCardTokenization(
                    viewLifecycleOwner,
                    billingInfo,
                    object : ResponseHandler,
                        RecurlyTokenizationHandler {
                        override fun onSuccess(token: String, type: String) {
                            binding.textApiText.text = " token: $token \n type: $type"
                        }

                        override fun onError(error: ErrorRecurly) {
                            val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                            binding.textApiText.text = gsonPretty.toJson(error)
                            error.fields.forEach {
                                if (it == "number"){
                                    binding.recurlyUnifiedView.setCreditCardNumberError()
                                } else if (it == "year" || it == "month"){
                                    binding.recurlyUnifiedView.setExpirationError()
                                } else if (it == "cvv")
                                    binding.recurlyUnifiedView.setCvvError()
                            }
                        }
                    })
            } else {
                var errorMessage = "Verify: \n"

                if (!validationData.first)
                    errorMessage += "Credit card number\n"
                if (!validationData.second)
                    errorMessage += "Expiration date\n"
                if (!validationData.third)
                    errorMessage += "CVV code\n"

                binding.textApiText.text = errorMessage
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}