package com.example.buildersclubpoc.agent.preferences

data class UserPreferences(
    val mealPreferences: MealPreferences,
    val groceryPreferences: GroceryPreferences,
    val diningPreferences: DiningPreferences,
)

data class MealPreferences(
    val style: String,
    val goal: String,
    val breakfastOptions: List<String>,
    val lunchOptions: List<String>,
    val dinnerOptions: List<String>,
)

data class GroceryPreferences(
    val fruits: List<String>,
    val vegetables: List<String>,
    val staples: List<String>,
)

data class DiningPreferences(
    val preferredAreas: List<String>,
    val preferredRestaurants: List<String>,
    val preferredCuisines: List<String>,
)

object MockUserPreferenceProfiles {
    val healthyBalancedProfile = UserPreferences(
        mealPreferences = MealPreferences(
            style = "Healthy",
            goal = "Balanced meals that feel light, nourishing, and work well on busy days.",
            breakfastOptions = listOf(
                "Greek yogurt bowl with berries and chia seeds",
                "Vegetable poha with sprouts",
                "Oats porridge with banana and nuts",
                "Paneer and multigrain toast breakfast plate",
            ),
            lunchOptions = listOf(
                "Grilled paneer bowl with brown rice and sauteed vegetables",
                "Dal, roti, sabzi, and salad combo",
                "Quinoa khichdi with curd",
                "Lean protein thali with greens",
            ),
            dinnerOptions = listOf(
                "Soup and grilled sandwich combo",
                "Millet roti with mixed veg curry",
                "Light rice bowl with stir-fried vegetables",
                "Paneer salad bowl with roasted vegetables",
            ),
        ),
        groceryPreferences = GroceryPreferences(
            fruits = listOf(
                "Bananas",
                "Apples",
                "Papaya",
                "Berries",
                "Oranges",
            ),
            vegetables = listOf(
                "Spinach",
                "Broccoli",
                "Carrots",
                "Cucumber",
                "Bell peppers",
                "Tomatoes",
            ),
            staples = listOf(
                "Greek yogurt",
                "Oats",
                "Brown rice",
                "Olive oil",
                "Mixed nuts",
            ),
        ),
        diningPreferences = DiningPreferences(
            preferredAreas = listOf(
                "Koramangala, Bengaluru",
                "Indiranagar, Bengaluru",
                "HSR Layout, Bengaluru",
                "Bellandur, Bengaluru",
            ),
            preferredRestaurants = listOf(
                "Salad Days",
                "FreshMenu",
                "Third Wave Coffee",
                "Truffles",
            ),
            preferredCuisines = listOf(
                "Healthy",
                "Indian",
                "Mediterranean",
                "Continental",
            ),
        ),
    )
}
