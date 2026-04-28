package com.example.myfirstandroidstudioapp_tictactoe

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    var activePlayer = "X"
    var gameActive = true
    var countX = 0
    var countO = 0
    var currentTheme = 0
    private var winningCombo: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val splash = findViewById<LinearLayout>(R.id.splashScreen)
        val sText = findViewById<TextView>(R.id.splashText)

        // START PLAYFUL ANIMATION
        playSplashAnimation(sText, splash)
    }

    // NEW: Playfully fades text in, holds, and fades out
    private fun playSplashAnimation(text: TextView, splashLayout: LinearLayout) {
        // Fade in
        val fadeIn = ObjectAnimator.ofFloat(text, "alpha", 0f, 1f).setDuration(1200)

        // Scale/Playful coming to screen effect
        val scaleX = ObjectAnimator.ofFloat(text, "scaleX", 0.7f, 1f).setDuration(1200)
        val scaleY = ObjectAnimator.ofFloat(text, "scaleY", 0.7f, 1f).setDuration(1200)

        // Combine animations
        val firstPart = AnimatorSet()
        firstPart.playTogether(fadeIn, scaleX, scaleY)
        firstPart.interpolator = AccelerateDecelerateInterpolator()

        // Fade out
        val fadeOut = ObjectAnimator.ofFloat(text, "alpha", 1f, 0f).setDuration(1200)
        fadeOut.startDelay = 1500 // Hold for 1.5 seconds

        // Sequential Play
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(firstPart, fadeOut)
        animatorSet.start()

        // HIDE SPLASH AFTER ANIMATION COMPLETE (~4 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            splashLayout.visibility = View.GONE
        }, 4100)
    }

    fun onBoxClicked(view: View) {
        if (!gameActive) return
        val cardView = view as CardView
        val textView = cardView.getChildAt(0) as TextView

        if (textView.text == "") {
            textView.text = activePlayer
            textView.setTextColor(Color.BLACK)

            if (checkWinner()) {
                if (activePlayer == "X") countX++ else countO++
                updateScore()
                drawWinningLine()
                showWin(activePlayer)
            }
            activePlayer = if (activePlayer == "X") "O" else "X"
        }
    }

    private fun checkWinner(): Boolean {
        val t = getTextViews()
        val wins = arrayOf(
            intArrayOf(0,1,2), intArrayOf(3,4,5), intArrayOf(6,7,8),
            intArrayOf(0,3,6), intArrayOf(1,4,7), intArrayOf(2,5,8),
            intArrayOf(0,4,8), intArrayOf(2,4,6)
        )
        for (p in wins) {
            if (t[p[0]].text != "" && t[p[0]].text == t[p[1]].text && t[p[1]].text == t[p[2]].text) {
                winningCombo = p
                return true
            }
        }
        return false
    }

    private fun drawWinningLine() {
        if (winningCombo == null) return
        val cards = getCardViews()
        val winHighlight = when(currentTheme) {
            1 -> "#C1E8FF"
            2 -> "#DAF1DE"
            else -> "#D391B0"
        }
        for (i in winningCombo!!) {
            cards[i].setCardBackgroundColor(Color.parseColor(winHighlight))
        }
    }

    private fun showWin(winner: String) {
        val p1 = findViewById<EditText>(R.id.p1Name).text.toString()
        val p2 = findViewById<EditText>(R.id.p2Name).text.toString()
        val winnerName = if (winner == "X") (if(p1.isEmpty()) "Player X" else p1) else (if(p2.isEmpty()) "Player O" else p2)
        Toast.makeText(this, "$winnerName Wins! 🏆", Toast.LENGTH_LONG).show()
        gameActive = false
    }

    private fun updateScore() {
        findViewById<TextView>(R.id.scoreX).text = "PLAYER X: $countX"
        findViewById<TextView>(R.id.scoreO).text = "PLAYER O: $countO"
    }

    fun resetBoard(view: View) {
        gameActive = true
        activePlayer = "X"
        winningCombo = null
        val boxColor = when(currentTheme) {
            1 -> "#5483B3"
            2 -> "#235347"
            else -> "#7B466A"
        }
        for (tv in getTextViews()) tv.text = ""
        for (card in getCardViews()) card.setCardBackgroundColor(Color.parseColor(boxColor))
    }

    fun resetScore(view: View) {
        countX = 0; countO = 0
        updateScore()
        resetBoard(view)
    }

    fun changeTheme(view: View) {
        currentTheme = (currentTheme + 1) % 3
        val root = findViewById<View>(R.id.rootLayout)
        val header = findViewById<TextView>(R.id.headerText)
        val sX = findViewById<TextView>(R.id.scoreX)
        val sO = findViewById<TextView>(R.id.scoreO)
        val name1 = findViewById<EditText>(R.id.p1Name)
        val name2 = findViewById<EditText>(R.id.p2Name)
        val tBtn = findViewById<Button>(R.id.themeBtn)
        val nBtn = findViewById<Button>(R.id.newGameBtn)
        val rBtn = findViewById<Button>(R.id.resetScoreBtn)
        val cards = getCardViews()

        when (currentTheme) {
            0 -> { // Palette 1
                root.setBackgroundColor(Color.parseColor("#0C0420"))
                header.setTextColor(Color.parseColor("#D391B0"))
                sX.setTextColor(Color.parseColor("#D391B0")); sO.setTextColor(Color.parseColor("#D391B0"))

                // NEW: Syncing Input Hint Colors
                name1.setHintTextColor(Color.parseColor("#9F6496"))
                name2.setHintTextColor(Color.parseColor("#9F6496"))

                tBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5D3C64"))
                nBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BA6E8F"))
                rBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#5D3C64"))
                for (c in cards) c.setCardBackgroundColor(Color.parseColor("#7B466A"))
            }
            1 -> { // Palette 2
                root.setBackgroundColor(Color.parseColor("#0C0420"))
                header.setTextColor(Color.parseColor("#C1E8FF"))
                sX.setTextColor(Color.parseColor("#C1E8FF")); sO.setTextColor(Color.parseColor("#C1E8FF"))

                // NEW: Syncing Input Hint Colors
                name1.setHintTextColor(Color.parseColor("#5483B3"))
                name2.setHintTextColor(Color.parseColor("#5483B3"))

                tBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#052659"))
                nBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7DA0CA"))
                rBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#052659"))
                for (c in cards) c.setCardBackgroundColor(Color.parseColor("#5483B3"))
            }
            2 -> { // Palette 3
                root.setBackgroundColor(Color.parseColor("#0B2B26"))
                header.setTextColor(Color.parseColor("#DAF1DE"))
                sX.setTextColor(Color.parseColor("#DAF1DE")); sO.setTextColor(Color.parseColor("#DAF1DE"))

                // NEW: Syncing Input Hint Colors
                name1.setHintTextColor(Color.parseColor("#8EB69B"))
                name2.setHintTextColor(Color.parseColor("#8EB69B"))

                tBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#163832"))
                nBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8EB69B"))
                rBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#163832"))
                for (c in cards) c.setCardBackgroundColor(Color.parseColor("#235347"))
            }
        }
    }

    private fun getCardViews() = arrayOf(
        findViewById<CardView>(R.id.card0), findViewById(R.id.card1), findViewById(R.id.card2),
        findViewById<CardView>(R.id.card3), findViewById(R.id.card4), findViewById(R.id.card5),
        findViewById<CardView>(R.id.card6), findViewById(R.id.card7), findViewById(R.id.card8)
    )

    private fun getTextViews() = arrayOf(
        findViewById<TextView>(R.id.tv0), findViewById<TextView>(R.id.tv1), findViewById<TextView>(R.id.tv2),
        findViewById<TextView>(R.id.tv3), findViewById<TextView>(R.id.tv4), findViewById<TextView>(R.id.tv5),
        findViewById<TextView>(R.id.tv6), findViewById<TextView>(R.id.tv7), findViewById<TextView>(R.id.tv8)
    )
}