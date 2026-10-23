package com.example.callblocker

import android.app.role.RoleManager
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var store: PrefixStore
    private lateinit var list: LinearLayout
    private lateinit var status: TextView

    private val roleRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            refreshStatus()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = PrefixStore(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }

        status = TextView(this).apply { textSize = 16f }
        val enableBtn = Button(this).apply {
            text = "Enable call blocking"
            setOnClickListener { requestScreeningRole() }
        }

        val input = EditText(this).apply {
            hint = "Prefix, e.g. +1400"
            inputType = InputType.TYPE_CLASS_PHONE
        }
        val addBtn = Button(this).apply {
            text = "Add prefix"
            setOnClickListener {
                if (store.add(input.text.toString())) {
                    input.text.clear()
                    renderList()
                } else {
                    Toast.makeText(context, "Invalid prefix", Toast.LENGTH_SHORT).show()
                }
            }
        }

        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        root.addView(status)
        root.addView(enableBtn)
        root.addView(input)
        root.addView(addBtn)
        root.addView(ScrollView(this).apply { addView(list) })
        setContentView(root)

        renderList()
        refreshStatus()
    }

    private fun requestScreeningRole() {
        val rm = getSystemService(RoleManager::class.java)
        if (rm.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
            !rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        ) {
            roleRequest.launch(rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
        }
    }

    private fun refreshStatus() {
        val rm = getSystemService(RoleManager::class.java)
        val on = rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        status.text = if (on) "Blocking is ACTIVE" else "Blocking is OFF – tap Enable"
    }

    private fun renderList() {
        list.removeAllViews()
        store.getAll().forEach { prefix ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            row.addView(TextView(this).apply {
                text = prefix
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
            })
            row.addView(Button(this).apply {
                text = "Remove"
                setOnClickListener { store.remove(prefix); renderList() }
            })
            list.addView(row)
        }
    }
}
