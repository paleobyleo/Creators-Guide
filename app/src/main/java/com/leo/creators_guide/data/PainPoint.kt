package com.leo.creators_guide.data

data class PainPoint(
    val id: Int,
    val category: String,
    val description: String,
    val frequency: Int, // 1-10 scale
    val solution: String,
    val estimatedCost: String, // Estimated cost for implementing the solution
    val costBreakdown: String, // Detailed breakdown of what the cost includes
    val costDetails: CostDetails // Structured cost information
)