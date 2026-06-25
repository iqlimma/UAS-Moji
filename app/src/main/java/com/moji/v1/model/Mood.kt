package com.moji.v1.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.moji.v1.R

enum class Mood(
    val label: String,
    val description: String,
    @ColorRes val backgroundColor: Int,
    @DrawableRes val cardBackground: Int,
    @DrawableRes val character: Int,
    @DrawableRes val barFill: Int
) {
    HAPPY(
        label = "Happy",
        description = "Today felt a little brighter!",
        backgroundColor = R.color.mood_happy_bg,
        cardBackground = R.drawable.bg_mood_happy,
        character = R.drawable.moji_happy,
        barFill = R.drawable.mood_bar_orange_fill
    ),
    NEUTRAL(
        label = "Neutral",
        description = "Just another day, and that's okay",
        backgroundColor = R.color.mood_neutral_bg,
        cardBackground = R.drawable.bg_mood_neutral,
        character = R.drawable.moji_neutral,
        barFill = R.drawable.mood_bar_pink_fill
    ),
    SAD(
        label = "Sad",
        description = "Taking it slow today...",
        backgroundColor = R.color.mood_sad_bg,
        cardBackground = R.drawable.bg_mood_sad,
        character = R.drawable.moji_sad,
        barFill = R.drawable.mood_bar_blue_fill
    ),
    TIRED(
        label = "Tired",
        description = "Low energy, need some rest",
        backgroundColor = R.color.mood_tired_bg,
        cardBackground = R.drawable.bg_mood_tired,
        character = R.drawable.moji_tired,
        barFill = R.drawable.mood_bar_green_fill
    ),
    ANGRY(
        label = "Angry",
        description = "Feeling a bit overwhelmed",
        backgroundColor = R.color.mood_angry_bg,
        cardBackground = R.drawable.bg_mood_angry,
        character = R.drawable.moji_angry,
        barFill = R.drawable.mood_bar_red_fill
    ),
    ENVY(
        label = "Envy",
        description = "Comparing yourself a bit today",
        backgroundColor = R.color.mood_envy_bg,
        cardBackground = R.drawable.bg_mood_envy,
        character = R.drawable.moji_envy,
        barFill = R.drawable.mood_bar_purple_fill) // Updated to the new purple bar from your friend    )
}