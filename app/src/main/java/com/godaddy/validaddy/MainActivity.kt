package com.godaddy.validaddy

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.StandardCharsets.UTF_8
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val TAG = "Mqtt"

    lateinit var mqttHelper: MqttHelper
    lateinit var approveButton: Button
    lateinit var denyButton: Button
    lateinit var messageTextView: TextView

    lateinit var id : String

    val PRIVATE_KEY =
        "MIIEowIBAAKCAQEAwYZC0cDxOJOjog6l+INbukZhzVSNk6bVpenwBtcqlNJeCtrRAuPFzXMLMzkVPZrywrkCCcBJi0hrSxGcuSifcj3CR/XyhEjuD2jxuPbpEbvZjpfSKonhX91OiQVItIVrsuj8sCZzcM+lsEoeLf63TrqjZdCToEiFZ0epWwLjbUwV3dS8tm5w6g1HMXYiLdDHBN+/gwOaHGdqucViY14CRW8pLMmSMT0VPJTnuz3WlF+C1vqMUj0pmvgNXyuP6EKtga0qlynF/0sImybkLGQl5vH5vYUVZMt/TNst82Ky1IAsY9blwd375vzf88LPS48YE+s7ToFa1lH3rZHS5qXgFQIDAQABAoIBAFUbC0WdOTftTZfWI00vz2YSz63x0jUtuGU3hPQtKa+699qEMki4DGP2hOVo5BtsiffyTqAlLPKQzYZ831RaNgySdl8ZHSbpv6+NMkhqZUMEPn/D6owEiNWUyJXfegLRbfSHG6EHt9rTpoTpg24sx0ZbM8JxwIpLekdaL3MzyPpmJ4umD59kraKZwq8RfvODSIiZvgaPntyAZUPyjuePkhi5XYlLMvypglIuJuhSUFybwHqQuyvo+OcVkhKI9hIEHeGH4qoGYHOKS+7ZkxCmHDZOVNlVLKfcJsxDqGdhqe0diCPR94ue8wpGkcfpJe4Ee1n9LGV0YwANNCAmBrqz+YECgYEA9+H27XVWaTAC0EIkIakRfOVB3tm50u4A+yjIWZLKWFs0V3HSoz88UxjJgfxLPyfuWr7liJ778bw+JH4eQzufMml1E61X/NJKDI+vNzcWO+KppFlHK0nYfARejfLU0eohFvljDeW1rZYEFIuYJ3BYadgWFKcg5sdYAClrZt6JCGECgYEAx9yalcfxqGzCqBm7dC9rsl5Xg7H8um8UX46lIGM656LfjNdkAcS1Bu2QLXi67aDLQAnJ1gewj5niILsUT61EgIrrdY5mvVqikbZOqpEeOcbvj7UkhQ8DVTX4yUvQdVn5kHX0TCJptctdt00+ojbpdmdQ9aKVCsV5AGU/L++JpDUCgYAmd2BUNRM1u4eUPc9RkeTu1rzEElFt1BU+5HNCwy63S1/x+e3P+24nDv+mNjqPEGlCl8ES1GkQeD/EdfXIRSZNMmRCq3GuAAwRqvJrxRakMGZW5KvQoeMAS8VIjdhMwuJjqEugynbI+zCi546zABIYSroSdmT6qxCS5dqO2hW1wQKBgDd4HZtGc+aH0PqwsZsMcjp9/pH5eygPHjtrLp8vizCwvpSrTwFCo+95TZOhN1guUVrDnLX/SlyAnAbzhS6b6zA13iWxUQhquEXysCCcyCPG98QNxUX8pbMnmJXdqcx4HVCfvB7JSkd/WtI96Q1CakdHY2vZJL5pkiyu7PuT4sKlAoGBAMtmZfeyA7+JXm/zQMTO4o2t+E4q2U1YMMFQDZ6qVnOEVlCcIgfL1VMBb5igN6Dvj0wAK3Ug5y6PVrlDeWVYMe3UgPmETgKWM0YXgzwfFpcUfwMCmg14SnrKuHBcC1l35+wRC80T+6Va/9/edG0ufF7ynWIRq5J8cPL/qhjTI0Y0"
    val PUBLIC_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuEKs82PrLgadNMe7LDXYQfaaF7S1ly9klijvhLolEhPHKiFKFJcXMbnA+u+1fSbUMADdCZDR3QItddS8RIVL6gEuUsN93mmXOuJobL543Vo7Fubw5PhB0ZJUkJqX3In/YbdOjfjCyojry9xBQrM5ogBAB18oJOwy8DUvyhX+sfqTeGuSipiRpZ/WgME+aaT9Se8Tkrd5PAW1cAFBkjBzm5XhV5ZWBYmAHpHM1Hcvedod0GHukZOEzP6IABL15rDE+Qpk4D9AX1GwQF3QMJgBFqYXdUjNZd6ebr0xLq27/DxxChkEo+Cd3v3/EiS2q2qlco+rIEBweANElhRYpNTrFQIDAQAB"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG,  "starting")
        setContentView(R.layout.activity_main)
        startMqtt()

        approveButton = findViewById(R.id.approve_button)
        denyButton = findViewById(R.id.deny_button)
        messageTextView = findViewById(R.id.message_textView)



        approveButton.setOnClickListener {
            mqttHelper.publishResponse(encrypt("approve,$id", generatePublicKey()))
            onResponseSent()
        }

        denyButton.setOnClickListener {
            mqttHelper.publishResponse(encrypt("deny,$id", generatePublicKey()))
            onResponseSent()
        }

        resetMessage()
    }

    private fun onResponseSent() {
        Toast.makeText(applicationContext, getString(R.string.response_toast), Toast.LENGTH_SHORT).show()
        resetMessage()
    }

    private fun resetMessage() {
        messageTextView.text = getString(R.string.default_message)
        approveButton.visibility = View.INVISIBLE
        denyButton.visibility = View.INVISIBLE
    }

    @Throws(Exception::class)
    fun encrypt(plainText: String, publicKey: PublicKey): String {
        val encryptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val cipherText = encryptCipher.doFinal(plainText.toByteArray(UTF_8))

        return Base64.getEncoder().encodeToString(cipherText)
    }

    @Throws(Exception::class)
    fun decrypt(cipherText: String, privateKey: PrivateKey): String {
        val bytes = Base64.getDecoder().decode(cipherText)

        val decriptCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding")
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey)

        return String(decriptCipher.doFinal(bytes), UTF_8)
    }

    fun generatePrivateKey(): PrivateKey {
        val kf = KeyFactory.getInstance("RSA")
        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(PRIVATE_KEY))
        val privKey = kf.generatePrivate(keySpecPKCS8)
        return privKey
    }

    private fun generatePublicKey(): PublicKey {
        //val keyBytes = readAllBytes(Paths.get("publicKey"))
        val kf = KeyFactory.getInstance("RSA")
        val keySpecX509 = X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY))
        val pubKey = kf.generatePublic(keySpecX509) as RSAPublicKey
        return pubKey
    }

    private fun startMqtt() {
        mqttHelper = MqttHelper(applicationContext)
        mqttHelper.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.i(TAG, "Connection Complete")
            }

            override fun connectionLost(throwable: Throwable) {
                Log.w(TAG, "Connection Lost")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w(TAG, mqttMessage.toString())

                messageTextView.text = cleanMessage(decrypt(mqttMessage.toString(), generatePrivateKey()))

                approveButton.visibility = View.VISIBLE
                denyButton.visibility = View.VISIBLE
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.i(TAG, "Delivery Complete")
            }
        })
    }

    private fun cleanMessage(s: String): String {
        val message = s.split(',')
        id = message.get(message.size - 1)
        val result = message.subList(0, message.size - 1)
        return result.joinToString(",")
    }

}