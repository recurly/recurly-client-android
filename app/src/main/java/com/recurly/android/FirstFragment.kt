package com.recurly.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.recurly.android.databinding.FragmentFirstBinding
import com.recurly.androidsdk.RecurlyClient
import com.recurly.androidsdk.data.model.tokenization.RecurlyBillingInfo
import com.recurly.androidsdk.data.model.tokenization.RecurlyException
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val recurlyClient = RecurlyClient(BuildConfig.RECURLY_PUBLIC_KEY)

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

        binding.testButton.setOnClickListener {
            val validationData = binding.recurlyUnifiedView.validateData()
            if (validationData.first && validationData.second && validationData.third) {
                binding.textApiText.text = "Loading..."
                val billingInfo = RecurlyBillingInfo(
                    firstName = "John",
                    lastName = "Doe",
                    company = "Recurly",
                    addressOne = "Address1",
                    city = "Boulder",
                    state = "Colorado",
                    postalCode = "00000",
                    country = "America"
                )
                val cardParams = binding.recurlyUnifiedView.cardParams()
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val token = recurlyClient.tokenize(cardParams, billingInfo)
                        binding.textApiText.text = " token: ${token.id} \n type: ${token.type}"
                    } catch (e: RecurlyException) {
                        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                        binding.textApiText.text = gsonPretty.toJson(e.error)
                        e.error.fields.forEach {
                            if (it == "number") {
                                binding.recurlyUnifiedView.setCreditCardNumberError()
                            } else if (it == "year" || it == "month") {
                                binding.recurlyUnifiedView.setExpirationError()
                            } else if (it == "cvv")
                                binding.recurlyUnifiedView.setCvvError()
                        }
                    }
                }
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