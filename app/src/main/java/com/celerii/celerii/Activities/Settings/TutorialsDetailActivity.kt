package com.celerii.celerii.Activities.Settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.QuoteSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.celerii.celerii.R
import com.celerii.celerii.helperClasses.CreateTextDrawable
import com.celerii.celerii.helperClasses.ImageGetter
import com.celerii.celerii.helperClasses.QuoteSpanClass
import com.celerii.celerii.helperClasses.SharedPreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_tutorials_detail.*

class TutorialsDetailActivity : AppCompatActivity() {
    var context: Context? = null
    var sharedPreferencesManager: SharedPreferencesManager? = null

    var auth: FirebaseAuth? = null
    var mFirebaseDatabase: FirebaseDatabase? = null
    var mDatabaseReference: DatabaseReference? = null
    var mFirebaseUser: FirebaseUser? = null

    var toolbar: Toolbar? = null

    var featureUseKey = ""
    var featureName = "Newsletter Detail"
    var sessionStartTime: Long = 0
    var sessionDurationInSeconds = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorials_detail)

        context = this
        sharedPreferencesManager = SharedPreferencesManager(context)

        auth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase!!.reference
        mFirebaseUser = auth!!.currentUser

        val b = intent.extras
        val title = b!!.getString("title", "Title")
        val poster = b.getString("poster", "Celerii")
        val youtubeVideoID = b.getString("youtubeVideoID", "")
        val body = b.getString("body", "We cannot display this tutorial at this time")

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Tutorials"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        if (youtubeVideoID.isEmpty()) {
            youtubeplayerview.visibility = View.GONE
        }

        tutorialtitle.text = title
        tutorialposter.text = poster
        getLifecycle().addObserver(youtubeplayerview)
        displayHtml(body)

        youtubeplayerview.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = youtubeVideoID
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })

        val textDrawable: Drawable
        if (poster.trim { it <= ' ' }.isNotEmpty()) {
            val nameArray = poster.replace("\\s+".toRegex(), " ").trim { it <= ' ' }.split(" ".toRegex()).toTypedArray()
            textDrawable = if (nameArray.size == 1) {
                CreateTextDrawable.createTextDrawable(context, nameArray[0], 40)
            } else {
                CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 40)
            }
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 40)
        }
        profilepicture.setImageDrawable(textDrawable)
    }

    private fun displayHtml(html: String) {

        // Creating object of ImageGetter class you just created
        val imageGetter = ImageGetter(resources, tutorialbody)

        // Using Html framework to parse html
        val styledText = HtmlCompat.fromHtml(html,
                HtmlCompat.FROM_HTML_MODE_LEGACY,
                imageGetter, null)

        replaceQuoteSpans(styledText as Spannable)
        imageClick(styledText as Spannable)

        // to enable image/link clicking
        tutorialbody.movementMethod = LinkMovementMethod.getInstance()

        // setting the text after formatting html and downloading and setting images
        tutorialbody.text = styledText
    }

    private fun replaceQuoteSpans(spannable: Spannable) {
        val quoteSpans: Array<QuoteSpan> =
                spannable.getSpans(0, spannable.length - 1, QuoteSpan::class.java)

        for (quoteSpan in quoteSpans) {
            val start: Int = spannable.getSpanStart(quoteSpan)
            val end: Int = spannable.getSpanEnd(quoteSpan)
            val flags: Int = spannable.getSpanFlags(quoteSpan)
            spannable.removeSpan(quoteSpan)
            spannable.setSpan(
                    QuoteSpanClass(
                            // background color
                            ContextCompat.getColor(this, R.color.colorPrimary),
                            // strip color
                            ContextCompat.getColor(this, R.color.colorAccent),
                            // strip width
                            10F, 50F
                    ),
                    start, end, flags
            )
        }
    }

    // Function to parse image tags and enable click events
    private fun imageClick(html: Spannable) {
        for (span in html.getSpans(0, html.length, ImageSpan::class.java)) {
            val flags = html.getSpanFlags(span)
            val start = html.getSpanStart(span)
            val end = html.getSpanEnd(span)
            html.setSpan(object : URLSpan(span.source) {
                override fun onClick(v: View) {
                    Log.d("TAG", "onClick: url is ${span.source}")
                }
            }, start, end, flags)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}