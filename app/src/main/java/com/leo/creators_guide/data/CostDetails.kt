package com.leo.creators_guide.data

data class CostDetails(
    val monthlyCost: String?, // Monthly recurring cost if applicable
    val oneTimeCost: String?, // One-time setup cost if applicable
    val annualCost: String?, // Annual cost if applicable
    val freeTier: String?, // Free tier details if available
    val premiumTier: String?, // Premium tier details if applicable
    val implementationTime: String?, // Estimated time to implement
    val requiredSkills: List<String>?, // Skills needed for implementation
    val toolsAndPlatforms: List<String>?, // Tools/platforms involved
    val ongoingMaintenance: String? // Ongoing maintenance costs
)