package com.pmdm.adogtale.ui


import com.pmdm.adogtale.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.yuyakaido.android.cardstackview.*
import com.pmdm.adogtale.controller.CardStackAdapter
import com.pmdm.adogtale.controller.CardStackCallback
import com.pmdm.adogtale.model.Itemx

class CardSwipeActivity : AppCompatActivity() {
    private val TAG = "CardSwipeActivity"
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: CardStackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_swipe)

        val cardStackView = findViewById<CardStackView>(R.id.card_stack_view)

        manager = CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {
                Log.d(TAG, "onCardDragging: d=" + direction?.name + " ratio=" + ratio)
            }

            override fun onCardSwiped(direction: Direction?) {
                Log.d(TAG, "onCardSwiped: p=" + manager.topPosition + " d=" + direction)
                when (direction) {
                    Direction.Right -> Toast.makeText(
                        this@CardSwipeActivity,
                        "Direction Right",
                        Toast.LENGTH_SHORT
                    ).show()

                    Direction.Top -> Toast.makeText(
                        this@CardSwipeActivity,
                        "Direction Top",
                        Toast.LENGTH_SHORT
                    ).show()

                    Direction.Left -> Toast.makeText(
                        this@CardSwipeActivity,
                        "Direction Left",
                        Toast.LENGTH_SHORT
                    ).show()

                    Direction.Bottom -> Toast.makeText(
                        this@CardSwipeActivity,
                        "Direction Bottom",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> {
                        Toast.makeText(this@CardSwipeActivity, "Take care", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                // Paginating
                if (manager.topPosition == adapter.itemCount - 5) {
                    paginate()
                }
            }

            override fun onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.topPosition)
            }

            override fun onCardCanceled() {
                Log.d(TAG, "onCardCanceled: " + manager.topPosition)
            }

            override fun onCardAppeared(view: View?, position: Int) {
                val tv = view?.findViewById<TextView>(R.id.item_name)
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv?.text)
            }

            override fun onCardDisappeared(view: View?, position: Int) {
                val tv = view?.findViewById<TextView>(R.id.item_name)
                Log.d(TAG, "onCardDisappeared: " + position + ", nama: " + tv?.text)
            }
        })

        with(manager) {
            setStackFrom(StackFrom.None)
            setVisibleCount(3)
            setTranslationInterval(8.0f)
            setScaleInterval(0.95f)
            setSwipeThreshold(0.3f)
            setMaxDegree(20.0f)
            setDirections(Direction.FREEDOM)
            setCanScrollHorizontal(true)
            setSwipeableMethod(SwipeableMethod.Manual)
            setOverlayInterpolator(LinearInterpolator())
        }
        // Callback Listener
        addList().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val items = task.result
                adapter = CardStackAdapter(items)
                cardStackView.layoutManager = manager
                cardStackView.adapter = adapter
                cardStackView.itemAnimator = DefaultItemAnimator()
            } else {
                Toast.makeText(this,"Could not to load database information", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun paginate() {
        val old = adapter.items

        // Calback Listener
        addList().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val baru = task.result
                val callback = CardStackCallback(old, baru)
                val hasil = DiffUtil.calculateDiff(callback)
                adapter.items = baru
                hasil.dispatchUpdatesTo(adapter)
            } else {
                Toast.makeText(this,"Could not to load database information", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addList(): Task<List<Itemx>> {
        val items = mutableListOf<Itemx>()
        val db = FirebaseFirestore.getInstance()

        return db.collection("profile").get().continueWith { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val name = document.getString("name") ?: ""
                    val age = document.getString("age") ?: ""
                    val user = document.getString("user") ?: ""

                    items.add(
                        Itemx(
                            "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg",
                            name,
                            age,
                            user
                        )
                    )
                }
            }

            items
        }
    }

}

/*
     private fun addList(): List<Itemx> {
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", name, age, user))
        items.add(Itemx("https://pamipe.com/wiki/wp-content/uploads/2022/09/Bichon-Maltes-2.jpg", name, age, user))
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", name, age, user))
        items.add(Itemx("https://www.kiwoko.com/blogmundoanimal/wp-content/uploads/2023/06/bichon-maltes-informacion.jpg", name, age, user))
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", name, age, user))

        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", "Markonah", "24", "Jember"))
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", "Marpuah", "20", "Malang"))
        items.add(Itemx("https://www.kiwoko.com/blogmundoanimal/wp-content/uploads/2023/06/bichon-maltes-informacion.jpg", "Sukijah", "27", "Jonggol"))
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", "Markobar", "19", "Bandung"))
        items.add(Itemx("https://pamipe.com/wiki/wp-content/uploads/2022/09/Bichon-Maltes-2.jpg", "Marmut", "25", "Hutan"))

        return items
    }*/



