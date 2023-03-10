package com.example.a223sqlitedatabasecrud

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.search.SearchBar

class MainActivity : AppCompatActivity() {

    lateinit var db:SQLiteDatabase
    lateinit var rs:Cursor
    lateinit var adapter: SimpleCursorAdapter
    lateinit var searchView: SearchView
    lateinit var listView: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var helper = MyHelper(applicationContext)
        db = helper.readableDatabase
        rs = db.rawQuery("SELECT * FROM ACTABLE ORDER BY NAME", null)


        val buttonFirst = findViewById<Button>(R.id.buttonFirst)
        val editName = findViewById<EditText>(R.id.editName)
        val editMeaning = findViewById<EditText>(R.id.editMeaning)
        val buttonNext = findViewById<Button>(R.id.buttonNext)
        val buttonPrevious = findViewById<Button>(R.id.buttonPrevious)
        val buttonLast = findViewById<Button>(R.id.buttonLast)
        val buttonInsert = findViewById<Button>(R.id.buttonInsert)
        val buttonUpdate = findViewById<Button>(R.id.buttonUpdate)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val buttonClear = findViewById<Button>(R.id.buttonClear)
        val buttonViewAll = findViewById<Button>(R.id.buttonViewAll)
        searchView = findViewById<SearchView>(R.id.searchView)
        listView = findViewById<ListView>(R.id.listView)

        adapter = SimpleCursorAdapter(
            applicationContext, android.R.layout.simple_expandable_list_item_2,
            rs, arrayOf("NAME", "MEANING"),
            intArrayOf(android.R.id.text1, android.R.id.text2), 0
        )
        listView.adapter = adapter

        registerForContextMenu(listView)

        buttonViewAll.setOnClickListener {
            adapter.notifyDataSetChanged()
            searchView.isIconified = false
            searchView.queryHint = "Search Amongst ${rs.count} Records "
            searchView.visibility = View.VISIBLE
            listView.visibility = View.VISIBLE
        }

        // show data in search View


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false

            }

            override fun onQueryTextChange(p0: String?): Boolean {
                rs = db.rawQuery(
                    "SELECT * FROM ACTABLE WHERE NAME LIKE '%${p0}%' OR MEANING LIKE '%${p0}%'",
                    null
                )
                adapter.changeCursor(rs)
                return false
            }
        })


        buttonFirst.setOnClickListener {
            if (rs.moveToFirst()) {
                editName.setText(rs.getString(1))
                editMeaning.setText(rs.getString(2))
            } else
                Toast.makeText(applicationContext, "Data Not Found", Toast.LENGTH_LONG).show()
        }

        buttonNext.setOnClickListener {
            if (rs.moveToNext()) {
                editName.setText(rs.getString(1))
                editMeaning.setText(rs.getString(2))

            } else if (rs.moveToFirst()) {
                editName.setText(rs.getString(1))
                editMeaning.setText(rs.getString(2))

            } else
                Toast.makeText(applicationContext, "Data Not Found", Toast.LENGTH_LONG).show()
        }

        buttonPrevious.setOnClickListener {
            if (rs.moveToPrevious()) {
                editName.setText(rs.getString(1))
                editMeaning.setText(rs.getString(2))
            } else if (rs.moveToLast()) {
                editName.setText(rs.getString(1))
                editMeaning.setText(rs.getString(2))
            } else
                Toast.makeText(applicationContext, "Data Not Found", Toast.LENGTH_LONG).show()
        }

        buttonLast.setOnClickListener {
            if (rs.moveToLast()) {
                editName.setText(rs.getString(1))
                editMeaning.setText(rs.getString(2))
            } else
                Toast.makeText(applicationContext, "Data Not Found", Toast.LENGTH_LONG).show()
        }

        buttonInsert.setOnClickListener {
            var cv = ContentValues()
            cv.put("NAME", editName.text.toString())
            cv.put("MEANING", editMeaning.text.toString())
            db.insert("ACTABLE", null, cv)
            rs.requery()

            var ad = AlertDialog.Builder(this)
            ad.setTitle("Add Record")
            ad.setMessage("Record is inserted successfully..")
            ad.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                editName.setText("")
                editMeaning.setText("")
                editName.requestFocus()

            })
            ad.show()
        }

        buttonClear.setOnClickListener {
            editName.setText("")
            editMeaning.setText("")
            editName.requestFocus()
        }

        buttonUpdate.setOnClickListener {
            var cv = ContentValues()
            cv.put("NAME", editName.text.toString())
            cv.put("MEANING", editMeaning.text.toString())
            db.update("ACTABLE", cv, "_id = ?", arrayOf(rs.getString(0)))
            rs.requery()

            var ad = AlertDialog.Builder(this)
            ad.setTitle("Update Record")
            ad.setMessage("Record Updated Successfully...")
            ad.setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                if (rs.moveToFirst()) {
                    editName.setText(rs.getString(1))
                    editMeaning.setText(rs.getString(2))
                }
            })
            ad.show()
        }

        buttonDelete.setOnClickListener {
            db.delete("ACTABLE", "_id = ?", arrayOf(rs.getString(0)))
            rs.requery()

            var ad = AlertDialog.Builder(this)
            ad.setTitle("Delete Record")
            ad.setMessage("Record Deleted Successfully...")
            ad.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                if (rs.moveToFirst()) {
                    editName.setText(rs.getString(1))
                    editMeaning.setText(rs.getString(2))
                } else {
                    editName.setText("No Data Found")
                    editMeaning.setText("No Data found ")
                }
            })
            ad.show()
        }


    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(101,11,1,"Delete")
        menu?.setHeaderTitle("removing of data")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        if(item.itemId ==11){
            db.delete("ACTABLE","_id =?",arrayOf(rs.getString(0)))
            rs.requery()
            adapter.notifyDataSetChanged()
            searchView.queryHint = "Search Amongst ${rs.count} Records "
        }

        return super.onContextItemSelected(item)
    }


}









