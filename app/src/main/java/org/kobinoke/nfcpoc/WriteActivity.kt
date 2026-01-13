package org.kobinoke.nfcpoc

import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.app.PendingIntent
import android.content.IntentFilter

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcF
import org.kobinoke.nfcpoc.R

class WriteActivity : AppCompatActivity() {

    private lateinit var editText: EditText

    private var intentFiltersArray: Array<IntentFilter>? = null
    private val techListsArray = arrayOf(
        arrayOf(Ndef::class.java.name),
        arrayOf(NdefFormatable::class.java.name)
    )

    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(this) }
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        editText = findViewById(R.id.edit_text)

        // Prepare PendingIntent
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        // Intent filter for TECH_DISCOVERED (catch all NDEF tags)
        val filter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        intentFiltersArray = arrayOf(filter)

        // Check NFC availability
        when {
            nfcAdapter == null -> Toast.makeText(this, "NFC not supported", Toast.LENGTH_SHORT).show()
            !nfcAdapter!!.isEnabled -> Toast.makeText(this, "Please turn on NFC", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onPause() {
        nfcAdapter?.disableForegroundDispatch(this)
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val message = editText.text.toString()
        if (message.isEmpty()) {
            Toast.makeText(this, "Write something first!", Toast.LENGTH_SHORT).show()
            return
        }

        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag == null) {
            Toast.makeText(this, "No NFC tag detected", Toast.LENGTH_SHORT).show()
            return
        }

        val nfcMessage = NdefMessage(
            arrayOf(NdefRecord.createTextRecord("en", message))
        )

        try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (ndef.isWritable) {
                    ndef.writeNdefMessage(nfcMessage)
                    Toast.makeText(this, "Successfully Written!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tag is read-only", Toast.LENGTH_SHORT).show()
                }
                ndef.close()
            } else {
                val format = NdefFormatable.get(tag)
                if (format != null) {
                    format.connect()
                    format.format(nfcMessage)
                    Toast.makeText(this, "Successfully Written!", Toast.LENGTH_SHORT).show()
                    format.close()
                } else {
                    Toast.makeText(this, "Tag is not NDEF compatible", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Write failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

