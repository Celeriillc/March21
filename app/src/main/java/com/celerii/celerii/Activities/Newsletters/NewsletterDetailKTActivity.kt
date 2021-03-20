package com.celerii.celerii.Activities.Newsletters

import android.content.Context
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo
import com.celerii.celerii.R
import com.celerii.celerii.helperClasses.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_newsletter_detail_k_t.*

class NewsletterDetailKTActivity : AppCompatActivity() {
    var context: Context? = null
    var sharedPreferencesManager: SharedPreferencesManager? = null

    var auth: FirebaseAuth? = null
    var mFirebaseDatabase: FirebaseDatabase? = null
    var mDatabaseReference: DatabaseReference? = null
    var mFirebaseUser: FirebaseUser? = null

//    private lateinit var database: DatabaseReference

    var toolbar: Toolbar? = null

//    var title: TextView? = null
//    var body:TextView? = null
//    var poster:TextView? = null
//    var date:TextView? = null
//    var expandedImage: ImageView? = null
    var parentActivity: String? = null

    var featureUseKey = ""
    var featureName = "Newsletter Detail"
    var sessionStartTime: Long = 0
    var sessionDurationInSeconds = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newsletter_detail_k_t)

        context = this
        sharedPreferencesManager = SharedPreferencesManager(context)

        auth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase!!.reference
        mFirebaseUser = auth!!.currentUser

        val b = intent.extras
        val title = b!!.getString("title", "Newsletter Title")
        val body = b.getString("body", "We cannot display this newsletter at this time")
        val poster = b.getString("poster", "School")
        val date = b.getString("date", "Date")
        val imageURL = b.getString("imageURL", "Image URL")
        parentActivity = b.getString("parentActivity")
        if (parentActivity != null) {
            if ((parentActivity!!.isNotEmpty())) {
                sharedPreferencesManager!!.activeAccount = parentActivity
                mDatabaseReference = mFirebaseDatabase!!.getReference("UserRoles")
                mDatabaseReference!!.child(sharedPreferencesManager!!.myUserID).child("role").setValue(parentActivity)
            }
        }

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        newslettertitle.text = title
        displayHtml(body)
        newsletterposter.text = poster
        newsletterdate.text = Date.getRelativeTimeSpan(date)

        Glide.with(this)
                .load(imageURL)
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .centerCrop()
                .into(expandedimage)
    }

    private fun displayHtml(html: String) {

        // Creating object of ImageGetter class you just created
        val imageGetter = ImageGetter(resources, newsletterbody)

        // Using Html framework to parse html
        val styledText = HtmlCompat.fromHtml(html,
                HtmlCompat.FROM_HTML_MODE_LEGACY,
                imageGetter, null)

        replaceQuoteSpans(styledText as Spannable)
        ImageClick(styledText as Spannable)

        // to enable image/link clicking
        newsletterbody.movementMethod = LinkMovementMethod.getInstance()

        // setting the text after formatting html and downloading and setting images
        newsletterbody.text = styledText
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
    fun ImageClick(html: Spannable) {
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
            if (parentActivity != null) {
                if (parentActivity == "Parent") {
                    val i = Intent(this, ParentMainActivityTwo::class.java)
                    val bundle = Bundle()
                    bundle.putString("Fragment Int", "3")
                    i.putExtras(bundle)
                    startActivity(i)
                } else if (parentActivity == "Teacher") {
                    val i = Intent(this, TeacherMainActivityTwo::class.java)
                    val bundle = Bundle()
                    bundle.putString("Fragment Int", "4")
                    i.putExtras(bundle)
                    startActivity(i)
                }
            }
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        featureUseKey = if (sharedPreferencesManager!!.activeAccount == "Parent") {
            Analytics.featureAnalytics("Parent", mFirebaseUser!!.uid, featureName)
        } else {
            Analytics.featureAnalytics("Teacher", mFirebaseUser!!.uid, featureName)
        }
        sessionStartTime = System.currentTimeMillis()
    }

    override fun onStop() {
        super.onStop()
        sessionDurationInSeconds = ((System.currentTimeMillis() - sessionStartTime) / 1000).toString()
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser!!.uid, sessionDurationInSeconds)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (parentActivity != null) {
            if (parentActivity == "Parent") {
                val i = Intent(this, ParentMainActivityTwo::class.java)
                val bundle = Bundle()
                bundle.putString("Fragment Int", "3")
                i.putExtras(bundle)
                startActivity(i)
            } else if (parentActivity == "Teacher") {
                val i = Intent(this, TeacherMainActivityTwo::class.java)
                val bundle = Bundle()
                bundle.putString("Fragment Int", "4")
                i.putExtras(bundle)
                startActivity(i)
            }
        }
    }
}