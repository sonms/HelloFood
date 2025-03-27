package com.purang.hellofood.models

data class HealthTipData(
    val tipTitle: String,
    val tipDescription: String
)

object HealthTips {
    fun getAll(): List<HealthTipData> {
        return listOf(
            HealthTipData(
                "Do a short 15-minute workout",
                "Even this short workout can help regulate cholesterol and blood sugar levels, reducing the risk of chronic diseases."
            ),
            HealthTipData(
                "Sit on an outdoor bench for a while",
                "It helps reduce anxiety and promotes brain health."
            ),
            HealthTipData(
                "Extend meal time by 5 minutes",
                "Slowing down your eating pace can help control intake and reduce the frequency of bloating."
            ),
            HealthTipData(
                "Prepare personalized snacks",
                "People who consistently consume caffeine have a significantly lower risk of Alzheimer's disease than those who do not."
            ),
            HealthTipData(
                "Eat nuts",
                "For those who need to manage cholesterol levels, eating nuts can help lower bad LDL cholesterol and raise good HDL cholesterol."
            ),
            HealthTipData(
                "Stand up from your chair and stretch",
                "Among newly diagnosed cancer patients, over 90,000 cases are primarily caused by prolonged sitting and lack of movement."
            ),
            HealthTipData(
                "Drink water consciously",
                "Adequate hydration helps flush out toxins from the body, maintain skin health, and boost energy. Aim to drink at least 8 glasses of water a day."
            )
        )
    }
}
