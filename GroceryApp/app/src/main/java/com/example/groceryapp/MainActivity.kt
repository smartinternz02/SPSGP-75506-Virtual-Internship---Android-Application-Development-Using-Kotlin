package com.example.groceryapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(),GroceryRVAdaptor.GroceryItemCLickInterface {
    lateinit var itemsRV:RecyclerView
    lateinit var addFB:FloatingActionButton
    lateinit var list: List<GroceryItems>
    lateinit var groceryRVAdaptor: GroceryRVAdaptor
    lateinit var groceryViewModel: GroceryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        itemsRV = findViewById(R.id.idRVItems)
        addFB = findViewById(R.id.idFABAdd)
        list= ArrayList<GroceryItems>()
        groceryRVAdaptor = GroceryRVAdaptor(list,this)
        itemsRV.layoutManager = LinearLayoutManager(this)
        itemsRV.adapter = groceryRVAdaptor
        val groceryRepository=GroceryRepository(GroceryDatabase(this))
        val factory = GroceryViewModelFactory(groceryRepository)
        groceryViewModel = ViewModelProvider(this,factory).get(GroceryViewModel::class.java)
        groceryViewModel.getAllGroceryItems().observe(this , Observer {
            groceryRVAdaptor.list = it
            groceryRVAdaptor.notifyDataSetChanged()
        })
        addFB.setOnClickListener{
            openDialog()
        }

    }
    fun openDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.grocery_add_dialog)
        val cancelBtn = dialog.findViewById<Button>(R.id.idBtnCancel)
        val addBtn = dialog.findViewById<Button>(R.id.idBtnAdd)
        val itemEdit = dialog.findViewById<EditText>(R.id.idEditItem)
        val itemPriceEdit = dialog.findViewById<EditText>(R.id.idEditItemPrice)
        val itemQuantityEdit = dialog.findViewById<EditText>(R.id.idEditItemQuantity)
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        addBtn.setOnClickListener {
            val itemName : String = itemEdit.text.toString()
            val itemPrice : String = itemPriceEdit.text.toString()
            val itemQuantity : String = itemQuantityEdit.text.toString()
            val qty:Int = itemQuantity.toInt()
            val pr :Int = itemPrice.toInt()
            if (itemName.isNotEmpty()&&itemPrice.isNotEmpty()&&itemQuantity.isNotEmpty()){
                val items = GroceryItems(itemName,qty,pr)
                groceryViewModel.insert(items)
                Toast.makeText(applicationContext,"Item Inserted..",Toast.LENGTH_SHORT).show()
                groceryRVAdaptor.notifyDataSetChanged()
                dialog.dismiss()
            }else{
                Toast.makeText(applicationContext,"Please Enter all the data..",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
    override fun onItemClick(groceryItems: GroceryItems) {
        groceryViewModel.delete(groceryItems)
        groceryRVAdaptor.notifyDataSetChanged()
        Toast.makeText(applicationContext,"Item Deleted..",Toast.LENGTH_SHORT).show()
    }
}