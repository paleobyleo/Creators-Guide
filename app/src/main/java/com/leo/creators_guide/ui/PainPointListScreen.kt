package com.leo.creators_guide.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.leo.creators_guide.data.PainPoint
import com.leo.creators_guide.data.PainPointRepository
import com.leo.creators_guide.data.CostDetails
import com.leo.creators_guide.ui.UpdateNotification
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PainPointListScreen() {
    var expandedCategory by remember { mutableStateOf<String?>(null) }
    var showTop20 by remember { mutableStateOf(false) }
    val allPainPoints = PainPointRepository.getPainPoints()
    val categories = PainPointRepository.getCategories()
    
    // Sort all pain points by frequency (descending) for top 20
    val top20PainPoints = allPainPoints.sortedByDescending { it.frequency }.take(20)
    
    // Group pain points by category
    val painPointsByCategory = categories.associateWith { category ->
        allPainPoints.filter { it.category == category }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Update notification
        var showUpdateNotification by remember { mutableStateOf(true) }
        
        if (showUpdateNotification) {
            UpdateNotification(
                onDismiss = { showUpdateNotification = false },
                onUpdate = { 
                    // In a real app, this would open the download URL
                    showUpdateNotification = false
                }
            )
        }
        
        // Centered header text
        Text(
            text = "Creator's Pain Points & Solutions (GUIDE)",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
            
        // Copyright line
        Text(
            text = "© ${Calendar.getInstance().get(Calendar.YEAR)} Paleo by Leo MIT License",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(vertical = 4.dp)
        )
            
        // Toggle for Top 20 vs All
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = !showTop20,
                onClick = { showTop20 = false },
                label = { Text("All Pain Points") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF8B4513), // SaddleBrown
                    labelColor = Color.White,
                    selectedContainerColor = Color(0xFF5D2E0C), // Darker brown for selected state
                    selectedLabelColor = Color.White
                )
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = showTop20,
                onClick = { showTop20 = true },
                label = { Text("Top 20") },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFF8B4513), // SaddleBrown
                    labelColor = Color.White,
                    selectedContainerColor = Color(0xFF5D2E0C), // Darker brown for selected state
                    selectedLabelColor = Color.White
                )
            )
        }
            
        // Category Filter
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = (expandedCategory == null),
                    onClick = { expandedCategory = null },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFF8B4513), // SaddleBrown
                        labelColor = Color.White,
                        selectedContainerColor = Color(0xFF5D2E0C), // Darker brown for selected state
                        selectedLabelColor = Color.White
                    )
                )
            }
                
            items(categories) { category ->
                FilterChip(
                    selected = (expandedCategory == category),
                    onClick = { 
                        expandedCategory = if (expandedCategory == category) null else category
                    },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFF8B4513), // SaddleBrown
                        labelColor = Color.White,
                        selectedContainerColor = Color(0xFF5D2E0C), // Darker brown for selected state
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
            
        // Pain Points List (takes remaining space)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showTop20) {
                // Show top 20 pain points
                items(top20PainPoints) { painPoint ->
                    PainPointItem(painPoint)
                }
            } else if (expandedCategory == null) {
                // Show all categories
                categories.forEach { category ->
                    val points = painPointsByCategory[category] ?: emptyList()
                    if (points.isNotEmpty()) {
                        item {
                            CategoryHeader(category = category)
                        }
                            
                        items(points) { painPoint ->
                            PainPointItem(painPoint)
                        }
                    }
                }
            } else {
                // Show only selected category
                val points = painPointsByCategory[expandedCategory] ?: emptyList()
                if (points.isNotEmpty()) {
                    item {
                        CategoryHeader(category = expandedCategory!!)
                    }
                        
                    items(points) { painPoint ->
                        PainPointItem(painPoint)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryHeader(category: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PainPointItem(painPoint: PainPoint) {
    var expanded by remember { mutableStateOf(false) }
    var showCosts by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp), // Increased horizontal padding to reduce width further
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8B4513) // SaddleBrown color
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Pain point description
            Text(
                text = painPoint.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White // White text for better contrast on brown background
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Category and frequency
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Category: ${painPoint.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f) // Slightly transparent white
                )
                Text(
                    text = "Frequency: ${painPoint.frequency}/10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f) // Slightly transparent white
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Solution section
            Column {
                // Toggle button for solution
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { expanded = !expanded },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5D2E0C), // Darker brown for button
                            contentColor = Color.White
                        )
                    ) {
                        Text(if (expanded) "Hide Solution" else "Show Solution")
                    }
                    
                    // Costs button
                    Button(
                        onClick = { showCosts = !showCosts },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5D2E0C), // Darker brown for button
                            contentColor = Color.White
                        )
                    ) {
                        Text(if (showCosts) "Hide Costs" else "Costs")
                    }
                }
                
                // Solution content (visible when expanded)
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recommended Solution:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = painPoint.solution,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    // Estimated costs
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estimated Costs: ${painPoint.estimatedCost}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    // Cost breakdown
                    if (painPoint.costBreakdown.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Breakdown: ${painPoint.costBreakdown}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    // Detailed cost information
                    if (painPoint.costDetails != null) {
                        val costDetails = painPoint.costDetails
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        if (costDetails.implementationTime != null) {
                            Text(
                                text = "Implementation Time: ${costDetails.implementationTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.freeTier != null) {
                            Text(
                                text = "Free Tier: ${costDetails.freeTier}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.premiumTier != null) {
                            Text(
                                text = "Premium Tier: ${costDetails.premiumTier}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.requiredSkills?.isNotEmpty() == true) {
                            Text(
                                text = "Required Skills: ${costDetails.requiredSkills.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.toolsAndPlatforms?.isNotEmpty() == true) {
                            Text(
                                text = "Tools/Platforms: ${costDetails.toolsAndPlatforms.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.ongoingMaintenance != null) {
                            Text(
                                text = "Ongoing Maintenance: ${costDetails.ongoingMaintenance}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                // Cost information (visible when Costs button is clicked)
                if (showCosts && !expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estimated Costs: ${painPoint.estimatedCost}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    // Cost breakdown
                    if (painPoint.costBreakdown.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Breakdown: ${painPoint.costBreakdown}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    // Detailed cost information
                    if (painPoint.costDetails != null) {
                        val costDetails = painPoint.costDetails
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        if (costDetails.implementationTime != null) {
                            Text(
                                text = "Implementation Time: ${costDetails.implementationTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.freeTier != null) {
                            Text(
                                text = "Free Tier: ${costDetails.freeTier}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.premiumTier != null) {
                            Text(
                                text = "Premium Tier: ${costDetails.premiumTier}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.requiredSkills?.isNotEmpty() == true) {
                            Text(
                                text = "Required Skills: ${costDetails.requiredSkills.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.toolsAndPlatforms?.isNotEmpty() == true) {
                            Text(
                                text = "Tools/Platforms: ${costDetails.toolsAndPlatforms.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        
                        if (costDetails.ongoingMaintenance != null) {
                            Text(
                                text = "Ongoing Maintenance: ${costDetails.ongoingMaintenance}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}