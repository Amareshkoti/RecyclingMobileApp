package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.RecyclingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    viewModel: RecyclingViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val logs by viewModel.recyclingLogs.collectAsStateWithLifecycle()
    val challenges by viewModel.weeklyChallenges.collectAsStateWithLifecycle()
    val badges by viewModel.earnedBadges.collectAsStateWithLifecycle()

    val totalWeight by viewModel.totalWeightKg.collectAsStateWithLifecycle()
    val totalEnergy by viewModel.totalEnergySavedHours.collectAsStateWithLifecycle()
    val totalWater by viewModel.totalWaterSavedCups.collectAsStateWithLifecycle()

    // Dialog for custom profile editor
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KidsBackground,
        bottomBar = {
            NavigationBar(
                containerColor = BentoGrayBg,
                modifier = Modifier
                    .navigationBarsPadding()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .border(
                        width = 1.dp,
                        color = BentoGrayBorder,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ),
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Forest Home") },
                    label = { Text("Home", fontWeight = FontWeight.Bold) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoGreenPrimary,
                        selectedTextColor = EcoGreenPrimary,
                        unselectedIconColor = CharcoalText.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalText.copy(alpha = 0.6f),
                        indicatorColor = EcoGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Log Recycle") },
                    label = { Text("Recycle!", fontWeight = FontWeight.Bold) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoGreenPrimary,
                        selectedTextColor = EcoGreenPrimary,
                        unselectedIconColor = CharcoalText.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalText.copy(alpha = 0.6f),
                        indicatorColor = EcoGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_log")
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = "Fun Missions") },
                    label = { Text("Missions", fontWeight = FontWeight.Bold) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoGreenPrimary,
                        selectedTextColor = EcoGreenPrimary,
                        unselectedIconColor = CharcoalText.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalText.copy(alpha = 0.6f),
                        indicatorColor = EcoGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_missions")
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Sticker Trophies") },
                    label = { Text("Badges", fontWeight = FontWeight.Bold) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoGreenPrimary,
                        selectedTextColor = EcoGreenPrimary,
                        unselectedIconColor = CharcoalText.copy(alpha = 0.6f),
                        unselectedTextColor = CharcoalText.copy(alpha = 0.6f),
                        indicatorColor = EcoGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_badges")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    profile = userProfile,
                    logs = logs,
                    challenges = challenges,
                    earnedBadges = badges,
                    totalWeight = totalWeight,
                    totalEnergy = totalEnergy,
                    totalWater = totalWater,
                    onEditProfileClick = { showProfileDialog = true },
                    onDeleteLog = { viewModel.deleteLog(it) },
                    onNavigateToLog = { selectedTab = 1 }
                )
                1 -> RecyclerScreen(
                    viewModel = viewModel,
                    onLogLogged = { }
                )
                2 -> ChallengesScreen(
                    challenges = challenges,
                    onClaimReward = { challengeId ->
                        viewModel.claimReward(challengeId) { }
                    },
                    onRefreshChallenges = { viewModel.resetChallenges() }
                )
                3 -> BadgesScreen(
                    earnedBadges = badges
                )
            }

            // Customize Profile Animal Avatar Dialog
            if (showProfileDialog) {
                ProfileCustomizationDialog(
                    currentName = userProfile?.name ?: "Eco Kid",
                    currentAvatar = userProfile?.avatarEmoji ?: "🦊",
                    onDismiss = { showProfileDialog = false },
                    onSave = { newName, newAvatar ->
                        viewModel.updateProfile(newName, newAvatar)
                        showProfileDialog = false
                    }
                )
            }
        }
    }
}

// ==========================================
// 1. DASHBOARD SCREEN: ECO FOREST & STATS (BENTO GRID STYLE)
// ==========================================
@Composable
fun DashboardScreen(
    profile: UserProfile?,
    logs: List<RecyclingLog>,
    challenges: List<WeeklyChallenge> = emptyList(),
    earnedBadges: List<EarnedBadge> = emptyList(),
    totalWeight: Double,
    totalEnergy: Double,
    totalWater: Double,
    onEditProfileClick: () -> Unit,
    onDeleteLog: (RecyclingLog) -> Unit,
    onNavigateToLog: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // 1. Header Section - Title & Profile trigger
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEditProfileClick() }
                ) {
                    // Kids avatar roundel with edit badge style
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(EcoGreenLight, CircleShape)
                            .border(1.5.dp, EcoGreenPrimary, CircleShape)
                            .testTag("avatar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.avatarEmoji ?: "🦊",
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Hi, ${profile?.name ?: "Leo"}!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = CharcoalText,
                            lineHeight = 22.sp
                        )
                        Text(
                            text = "Level ${profile?.level ?: 1} Eco-Guardian",
                            fontSize = 12.sp,
                            color = CharcoalText.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onEditProfileClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(BentoGrayBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Customization",
                        tint = CharcoalText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 2. Bento Grid Live Photo / Illustration Card
        item {
            BentoForestIllustrationCard(level = profile?.level ?: 1, logs = logs)
        }

        // 3. Bento Card 1: Our Planet Impact Hero Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EcoGreenLight),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Massive background recycling logo
                    Text(
                        text = "♻️",
                        fontSize = 90.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .alpha(0.08f)
                            .offset(x = 12.dp, y = 16.dp)
                    )

                    Column {
                        Text(
                            text = "OUR PLANET IMPACT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EcoGreenDark.copy(alpha = 0.8f),
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f", totalWeight),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = EcoGreenDark,
                                lineHeight = 42.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "kg",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenDark,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = KidsWhite.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "SAVED FROM LANDFILL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EcoGreenDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            val progressVal = (totalWeight / 50.0).coerceIn(0.0, 1.0).toFloat()
                            LinearProgressIndicator(
                                progress = { progressVal },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = EcoGreenPrimary,
                                trackColor = KidsWhite.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Bento Card 2 & 3 Side-By-Side: Quick Add item & Weekly challenge preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Purple Log Card "Add Item"
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoPurpleBg),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .border(1.dp, BentoPurpleBorder, RoundedCornerShape(24.dp))
                        .clickable { onNavigateToLog() }
                        .testTag("bento_quick_log")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(KidsWhite, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add log",
                                tint = BentoPurpleText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Add Item",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CharcoalText
                            )
                            Text(
                                text = "Paper, Plastic, Glass, Metal Bins",
                                fontSize = 10.sp,
                                color = CharcoalText.copy(alpha = 0.7f),
                                lineHeight = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(KidsWhite.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+1 Log",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoPurpleText
                            )
                        }
                    }
                }

                // Coral Challenge Card "Weekly Task"
                val liveMission = challenges.firstOrNull { !it.isRewardClaimed } ?: challenges.firstOrNull()
                val liveTitle = liveMission?.title?.take(18) ?: "5 Bottles Task"
                val liveTarget = liveMission?.targetCount ?: 5
                val liveCurrent = liveMission?.currentCount ?: 3
                val liveFinished = liveCurrent >= liveTarget
                val labelComplete = if (liveFinished) "Done! 🎉" else "$liveCurrent/$liveTarget Done!"

                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoCoralBg),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .border(1.dp, BentoCoralBorder, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(KidsWhite, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Missions list",
                                tint = BentoCoralText,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Weekly Task",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = BentoCoralText
                            )
                            Text(
                                text = liveTitle,
                                fontSize = 10.sp,
                                color = CharcoalText.copy(alpha = 0.7f),
                                lineHeight = 12.sp
                            )
                        }

                        Text(
                            text = labelComplete,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoCoralText,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // 4. Bento Card 4: Badge Gallery Area
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoBlueBg),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBlueBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "EARNED STICKERS & BADGES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoBlueText.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val firstLogEarned = earnedBadges.any { it.badgeId == "badge_first_log" }
                        val streak3Earned = earnedBadges.any { it.badgeId == "badge_streak_3" }
                        val level3Earned = earnedBadges.any { it.badgeId == "badge_level_3" }

                        BentoBadgeItem(
                            emoji = "🌱",
                            title = "Sprout",
                            isUnlocked = firstLogEarned
                        )
                        BentoBadgeItem(
                            emoji = "🔥",
                            title = "Streak",
                            isUnlocked = streak3Earned,
                            isHighlighted = true
                        )
                        BentoBadgeItem(
                            emoji = "👑",
                            title = "Protector",
                            isUnlocked = level3Earned
                        )
                    }
                }
            }
        }

        // 5. Level Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KidsWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftGrayOutline, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    val crXp = profile?.xpInCurrentLevel ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LEVEL ${profile?.level ?: 1} PROGRESS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "$crXp/100 XP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = EcoGreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { crXp / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = EcoGreenPrimary,
                        trackColor = SoftGrayOutline.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // 6. Educational Equivalents row inside bento container
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KidsWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftGrayOutline, RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ImpactStatItem(
                        emoji = "🔥",
                        value = "${profile?.streakCount ?: 1} Days",
                        label = "Eco Streak",
                        bgColor = SolarYellowTertiary.copy(alpha = 0.15f)
                    )

                    ImpactStatItem(
                        emoji = "⚡",
                        value = String.format(Locale.getDefault(), "%.1fh", totalEnergy),
                        label = "Energy Saved",
                        bgColor = SkyBlueLight
                    )

                    ImpactStatItem(
                        emoji = "💧",
                        value = String.format(Locale.getDefault(), "%.1f", totalWater) + " Cups",
                        label = "Water Saved",
                        bgColor = EcoGreenLight
                    )
                }
            }
        }

        // 7. Did You Know Tip Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SolarYellowTertiary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SolarYellowTertiary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💡", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Did you know?",
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Recycled paper saves 17 trees! Every bottle you log makes a real difference! Keep up the magic!",
                            fontSize = 11.sp,
                            color = CharcoalText.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // 8. Recent Log title & logs listing
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Recycling Log Book 📖",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = CharcoalText
                )
                if (logs.isEmpty()) {
                    TextButton(
                        onClick = onNavigateToLog,
                        colors = ButtonDefaults.textButtonColors(contentColor = EcoGreenPrimary)
                    ) {
                        Text("Add First +", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (logs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗑️", fontSize = 48.sp, modifier = Modifier.alpha(0.4f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No logs yet this week!",
                            color = CharcoalText.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Tap the 'Recycle!' button below to start!",
                            color = CharcoalText.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else {
            items(logs.take(6)) { log ->
                LogItemRow(log = log, onDelete = { onDeleteLog(log) })
            }
        }
    }
}

@Composable
fun BentoForestIllustrationCard(
    modifier: Modifier = Modifier,
    level: Int = 1,
    logs: List<RecyclingLog> = emptyList()
) {
    // Derived values from recycling logs
    val paperCount = logs.filter { it.itemType.equals("paper", true) }.sumOf { it.quantity }
    val plasticCount = logs.filter { it.itemType.equals("plastic", true) }.sumOf { it.quantity }
    val glassCount = logs.filter { it.itemType.equals("glass", true) }.sumOf { it.quantity }
    val metalCount = logs.filter { it.itemType.equals("metal", true) }.sumOf { it.quantity }

    // 1. Trees (driven by Paper): Base of 1 tree, +1 for every 2 papers recycled (max 6)
    val maxTrees = 6
    val treesCount = (1 + paperCount / 2).coerceAtMost(maxTrees)
    val treeAssets = listOf(
        Triple("🌲", Alignment.BottomStart, Pair(16.dp, (-12).dp) to 32.sp),
        Triple("🌲", Alignment.BottomEnd, Pair((-38).dp, (-4).dp) to 40.sp),
        Triple("🌳", Alignment.BottomStart, Pair(42.dp, (-6).dp) to 38.sp),
        Triple("🌳", Alignment.BottomEnd, Pair((-12).dp, (-10).dp) to 30.sp),
        Triple("🌴", Alignment.BottomStart, Pair(64.dp, (-10).dp) to 34.sp),
        Triple("🎋", Alignment.BottomEnd, Pair((-60).dp, (-8).dp) to 32.sp)
    )

    // 2. Animals (driven by Plastic): Base of 1 (fox), +1 for every 2 plastics recycled (max 6)
    val maxAnimals = 6
    val animalsCount = (1 + plasticCount / 2).coerceAtMost(maxAnimals)
    val animalAssets = listOf(
        Triple("🦊", Alignment.BottomStart, Pair(88.dp, (-6).dp) to 24.sp),
        Triple("🐿️", Alignment.BottomEnd, Pair((-78).dp, (-2).dp) to 20.sp),
        Triple("🐇", Alignment.BottomStart, Pair(30.dp, (-2).dp) to 22.sp),
        Triple("🦔", Alignment.BottomEnd, Pair((-25).dp, (-2).dp) to 22.sp),
        Triple("🦌", Alignment.BottomStart, Pair(115.dp, (-8).dp) to 24.sp),
        Triple("🐨", Alignment.BottomEnd, Pair((-110).dp, (-6).dp) to 24.sp)
    )

    // 3. Flowers (driven by Glass): Base of 1, +1 for every 2 glass items recycled (max 6)
    val maxFlowers = 6
    val flowersCount = (1 + glassCount / 2).coerceAtMost(maxFlowers)
    val flowerAssets = listOf(
        Triple("🌸", Alignment.BottomStart, Pair(110.dp, (-2).dp) to 14.sp),
        Triple("🌻", Alignment.BottomEnd, Pair((-105).dp, (-4).dp) to 16.sp),
        Triple("🌷", Alignment.BottomStart, Pair(5.dp, (-2).dp) to 14.sp),
        Triple("🍄", Alignment.BottomEnd, Pair((-125).dp, (-2).dp) to 14.sp),
        Triple("🌹", Alignment.BottomStart, Pair(75.dp, (-2).dp) to 16.sp),
        Triple("🌺", Alignment.BottomEnd, Pair((-48).dp, (-2).dp) to 14.sp)
    )

    // 4. Sky Friends/Insects (driven by Metal): Base of 0, +1 for every 2 metal items recycled (max 6)
    val maxFriends = 6
    val friendsCount = (metalCount / 2).coerceAtMost(maxFriends)
    val friendAssets = listOf(
        Triple("🦋", Alignment.TopStart, Pair(30.dp, 45.dp) to 18.sp),
        Triple("🐝", Alignment.TopEnd, Pair((-30).dp, 50.dp) to 16.sp),
        Triple("🐦", Alignment.TopStart, Pair(120.dp, 60.dp) to 18.sp),
        Triple("🦉", Alignment.TopEnd, Pair((-120).dp, 55.dp) to 20.sp),
        Triple("🐞", Alignment.TopCenter, Pair((-80).dp, 35.dp) to 16.sp),
        Triple("🦆", Alignment.TopCenter, Pair(80.dp, 30.dp) to 18.sp)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(185.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF81D4FA), // Soft beautiful sky blue
                            Color(0xFFE1F5FE), // Light sky mist
                            Color(0xFFE8F5E9)  // Soft meadow mint
                        )
                    )
                )
        ) {
            // Golden glowing sun in background
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = (-10).dp, y = (-10).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFFFEB3B),
                                Color(0xFFFFCA28).copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Clouds
            Text(
                text = "☁️",
                fontSize = 28.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 60.dp, y = 20.dp)
                    .alpha(0.8f)
            )

            Text(
                text = "☁️",
                fontSize = 22.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-50).dp, y = 15.dp)
                    .alpha(0.7f)
            )

            // Custom green valleys drawn layered on bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFC8E6C9), // Light hill surface
                                Color(0xFF81C784)  // Rich hill shadow
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp)
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                EcoGreenLight,
                                Color(0xFF4CAF50)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 120.dp, topEnd = 60.dp)
                    )
            )

            // Center: Big smiling Earth/Globe representing "Our Planet"
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌍",
                    fontSize = 62.sp,
                    modifier = Modifier.alpha(0.95f)
                )
                Text(
                    text = "Your Protected Eco-Forest",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = EcoGreenDark,
                    modifier = Modifier
                        .background(KidsWhite.copy(alpha = 0.82f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Text(
                    text = "🌲x$treesCount 🦊x$animalsCount 🌸x$flowersCount" + (if (friendsCount > 0) " 🦋x$friendsCount" else ""),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    color = EcoGreenDark,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .background(KidsWhite.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }

            // Render active Trees
            for (i in 0 until treesCount) {
                val asset = treeAssets[i]
                Text(
                    text = asset.first,
                    fontSize = asset.third.second,
                    modifier = Modifier
                        .align(asset.second)
                        .offset(x = asset.third.first.first, y = asset.third.first.second)
                )
            }

            // Render active Animals
            for (i in 0 until animalsCount) {
                val asset = animalAssets[i]
                Text(
                    text = asset.first,
                    fontSize = asset.third.second,
                    modifier = Modifier
                        .align(asset.second)
                        .offset(x = asset.third.first.first, y = asset.third.first.second)
                )
            }

            // Render active Flowers
            for (i in 0 until flowersCount) {
                val asset = flowerAssets[i]
                Text(
                    text = asset.first,
                    fontSize = asset.third.second,
                    modifier = Modifier
                        .align(asset.second)
                        .offset(x = asset.third.first.first, y = asset.third.first.second)
                )
            }

            // Render active Sky Friends
            for (i in 0 until friendsCount) {
                val asset = friendAssets[i]
                Text(
                    text = asset.first,
                    fontSize = asset.third.second,
                    modifier = Modifier
                        .align(asset.second)
                        .offset(x = asset.third.first.first, y = asset.third.first.second)
                )
            }

            // Dynamic level celebration visual overlays
            if (level >= 3) {
                Text(
                    text = "✨",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(x = (-40).dp, y = 40.dp)
                )
                Text(
                    text = "✨",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(x = 45.dp, y = 55.dp)
                )
            }
        }
    }
}

@Composable
fun BentoBadgeItem(
    emoji: String,
    title: String,
    isUnlocked: Boolean,
    isHighlighted: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked) {
                        if (isHighlighted) KidsWhite.copy(alpha = 0.85f) else KidsWhite.copy(alpha = 0.45f)
                    } else {
                        KidsWhite.copy(alpha = 0.15f)
                    }
                )
                .border(
                    width = if (isHighlighted && isUnlocked) 2.dp else 1.dp,
                    color = if (isUnlocked) BentoBlueAccent else KidsWhite.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                Text(text = emoji, fontSize = 28.sp)
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "locked badge sticker",
                    tint = BentoBlueText.copy(alpha = 0.45f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = BentoBlueText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ImpactStatItem(
    emoji: String,
    value: String,
    label: String,
    bgColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .width(86.dp)
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = CharcoalText,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = CharcoalText.copy(alpha = 0.6f),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LogItemRow(
    log: RecyclingLog,
    onDelete: () -> Unit
) {
    val df = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val formattedTime = df.format(Date(log.timestamp))

    val (emoji, color) = when (log.itemType.lowercase()) {
        "paper" -> Pair("📚", EcoGreenPrimary)
        "plastic" -> Pair("🥤", SkyBlueSecondary)
        "glass" -> Pair("🔮", SolarYellowTertiary)
        "metal" -> Pair("🥫", CoralOrange)
        else -> Pair("📦", EcoGreenPrimary)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = KidsWhite),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${log.quantity}x ${log.materialName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CharcoalText
                )
                Text(
                    text = "${String.format(Locale.getDefault(), "%.2f", log.weightKg)} kg kept out • $formattedTime",
                    fontSize = 10.sp,
                    color = CharcoalText.copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_log_${log.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete entry",
                    tint = CoralOrange.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ==========================================
// 2. RECYCLER SCREEN: LOG RECYCLING
// ==========================================
@Composable
fun RecyclerScreen(
    viewModel: RecyclingViewModel,
    onLogLogged: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf("paper") }
    var quantity by remember { mutableStateOf(1) }
    var customLabelName by remember { mutableStateOf("") }
    
    // Celebration state
    var showCelebDialog by remember { mutableStateOf(false) }
    var lastSavedWeight by remember { mutableStateOf(0.0) }

    // Quick descriptor suggestions for kids
    val paperQuickSuggestions = listOf("Cereal Box 📚", "School Paper 📝", "Cardboard Box 📦", "Newspaper 📰")
    val plasticQuickSuggestions = listOf("Juice Bottle 🥤", "Milk Container 🍼", "Yogurt Cup 🥛", "Toy Wrapper 🛡️")
    val glassQuickSuggestions = listOf("Jam Jar 🥫", "Soda Bottle 🔮", "Kids Drinking Cup 💎", "Glass Bottle 🏺")
    val metalQuickSuggestions = listOf("Soda Can 🥫", "Soup Tin 🥘", "Alfoil Wrapping 🐚", "Metal Cap 🎖️")

    val activeQuickSuggestions = when (selectedType) {
        "paper" -> paperQuickSuggestions
        "plastic" -> plasticQuickSuggestions
        "glass" -> glassQuickSuggestions
        "metal" -> metalQuickSuggestions
        else -> paperQuickSuggestions
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Log Your Magic Deed! 🌍✨",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = EcoGreenDark
                )
                Text(
                    text = "Tap a cartoon item bin below, select quantity, and see the wonders you do!",
                    fontSize = 11.sp,
                    color = CharcoalText.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Bins Choice Selection Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Select what you recycled:",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RecyclerBinOption(
                        title = "Paper",
                        emoji = "📚",
                        color = EcoGreenPrimary,
                        isSelected = selectedType == "paper",
                        onClick = {
                            selectedType = "paper"
                            customLabelName = "Cardboard / Paper Box"
                        },
                        modifier = Modifier.weight(1f).testTag("bin_paper")
                    )
                    RecyclerBinOption(
                        title = "Plastic",
                        emoji = "🥤",
                        color = SkyBlueSecondary,
                        isSelected = selectedType == "plastic",
                        onClick = {
                            selectedType = "plastic"
                            customLabelName = "Plastic Cup"
                        },
                        modifier = Modifier.weight(1f).testTag("bin_plastic")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RecyclerBinOption(
                        title = "Glass",
                        emoji = "🔮",
                        color = SolarYellowTertiary,
                        isSelected = selectedType == "glass",
                        onClick = {
                            selectedType = "glass"
                            customLabelName = "Glass Jar"
                        },
                        modifier = Modifier.weight(1f).testTag("bin_glass")
                    )
                    RecyclerBinOption(
                        title = "Metal",
                        emoji = "🥫",
                        color = CoralOrange,
                        isSelected = selectedType == "metal",
                        onClick = {
                            selectedType = "metal"
                            customLabelName = "Aluminum Can"
                        },
                        modifier = Modifier.weight(1f).testTag("bin_metal")
                    )
                }
            }
        }

        // Stepper Quantity Picker
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KidsWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftGrayOutline, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "How many items did you recycle? 🔢",
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Minus button
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(EcoGreenLight, CircleShape)
                                .clickable { if (quantity > 1) quantity-- }
                                .testTag("btn_minus"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Fewer",
                                tint = EcoGreenDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        Text(
                            text = quantity.toString(),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = CharcoalText,
                            modifier = Modifier.width(60.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.width(32.dp))

                        // Plus button
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(EcoGreenLight, CircleShape)
                                .clickable { quantity++ }
                                .testTag("btn_plus"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "More",
                                tint = EcoGreenDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick descriptions & custom input
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "What item is this? (Tap an icon or enter below):",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText,
                    fontSize = 13.sp
                )

                // Quick horizontal list
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(activeQuickSuggestions) { suggestion ->
                        val cleanedSugg = suggestion.substringBeforeLast(" ")
                        val isSelected = customLabelName.lowercase() == cleanedSugg.lowercase()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    if (isSelected) EcoGreenLight else KidsWhite,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EcoGreenPrimary else SoftGrayOutline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { customLabelName = cleanedSugg }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = suggestion, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CharcoalText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Custom Text Input field
                OutlinedTextField(
                    value = customLabelName,
                    onValueChange = { customLabelName = it },
                    label = { Text("What did you recycle?", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoGreenPrimary,
                        unfocusedBorderColor = SoftGrayOutline,
                        focusedLabelColor = EcoGreenDark,
                        unfocusedLabelColor = CharcoalText,
                        focusedTextColor = CharcoalText,
                        unfocusedTextColor = CharcoalText
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_label_input")
                )
            }
        }

        // Submit Button
        item {
            val displayName = customLabelName.ifBlank { "Recycled Content" }
            Button(
                onClick = {
                    viewModel.logRecycling(selectedType, quantity, displayName) { weightSaved ->
                        lastSavedWeight = weightSaved
                        showCelebDialog = true
                        // Reset forms
                        quantity = 1
                        customLabelName = ""
                        onLogLogged()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("log_recycle_submit"),
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenDark),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TAP TO LOG IN MY TRACKER! 🚀", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }

    // Interactive Celebration Confetti Dialog
    if (showCelebDialog) {
        CelebrationDialog(
            itemType = selectedType,
            quantity = quantity,
            weightSaved = lastSavedWeight,
            onDismiss = { showCelebDialog = false }
        )
    }
}

@Composable
fun RecyclerBinOption(
    title: String,
    emoji: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) color.copy(alpha = 0.15f) else KidsWhite)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) color else SoftGrayOutline,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = CharcoalText
            )
        }
    }
}

// ==========================================
// 3. CHALLENGES SCREEN: PROGRESS & REWARDS
// ==========================================
@Composable
fun ChallengesScreen(
    challenges: List<WeeklyChallenge>,
    onClaimReward: (String) -> Unit,
    onRefreshChallenges: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Weekly Fun Missions! 🏆🚀",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = EcoGreenDark
                )
                Text(
                    text = "Finish these challenges to earn extra XP and level up your custom pet!",
                    fontSize = 11.sp,
                    color = CharcoalText.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (challenges.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading challenges...", color = CharcoalText.copy(alpha = 0.5f))
                }
            }
        } else {
            items(challenges) { challenge ->
                ChallengeCardItem(challenge = challenge, onClaim = { onClaimReward(challenge.challengeId) })
            }
        }

        // Fun administrative reset for testing or restarting board
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = onRefreshChallenges,
                    colors = ButtonDefaults.textButtonColors(contentColor = CoralOrange)
                ) {
                    Text("🔄 Swap/Reset Weekly Missions", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ChallengeCardItem(
    challenge: WeeklyChallenge,
    onClaim: () -> Unit
) {
    val progressPercent = challenge.currentCount.toFloat() / challenge.targetCount.toFloat()
    val isFulfilled = challenge.currentCount >= challenge.targetCount

    Card(
        colors = CardDefaults.cardColors(containerColor = KidsWhite),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isFulfilled && !challenge.isRewardClaimed) 3.dp else 1.dp,
                color = if (isFulfilled && !challenge.isRewardClaimed) SolarYellowTertiary else SoftGrayOutline,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Challenge Title
                Text(
                    text = challenge.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = CharcoalText,
                    modifier = Modifier.weight(1.2f)
                )

                // Reward XP badge
                Surface(
                    color = EcoGreenLight,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "+${challenge.rewardXp} XP 🎁",
                        color = EcoGreenDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body description
            Text(
                text = challenge.description,
                fontSize = 11.sp,
                color = CharcoalText.copy(alpha = 0.7f),
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress status text & slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isFulfilled) "Mission Cleared! 🎉" else "My Progress:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFulfilled) EcoGreenDark else CharcoalText
                )
                Text(
                    text = "${challenge.currentCount}/${challenge.targetCount}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = EcoGreenDark
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = if (isFulfilled) EcoGreenDark else SkyBlueSecondary,
                trackColor = EcoGreenLight
            )

            // Claim buttons
            if (isFulfilled) {
                Spacer(modifier = Modifier.height(12.dp))
                if (!challenge.isRewardClaimed) {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = SolarYellowTertiary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("claim_reward_${challenge.challengeId}")
                    ) {
                        Text("CLAIM REWARD! 🎁✨", fontWeight = FontWeight.Black, color = CharcoalText, fontSize = 12.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EcoGreenLight, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Earned", tint = EcoGreenDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("REWARD REGISTERED! (+${challenge.rewardXp} XP)", color = EcoGreenDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. BADGES SCREEN: MY STICKER ALBUM
// ==========================================
@Composable
fun BadgesScreen(
    earnedBadges: List<EarnedBadge>,
    modifier: Modifier = Modifier
) {
    // Definining standard full suite of badges so children can inspect unlocked AND locked badges
    val standardBadges = listOf(
        Pair("first_log", Triple("Green 🌱 Sprout", "Completed your very first recycling entry!", "🌱")),
        Pair("plastic_patrol", Triple("Plastic Patrol 🥤", "Kept 10 plastic bottles or wrappers safe from harm!", "🥤")),
        Pair("forest_hero", Triple("Forest Guardian 📚", "Recycled 15 paper products to keep forests happy and green!", "🌲")),
        Pair("glass_wizard", Triple("Glass Glass-adiator 🔮", "Kept 5 sharp glass jars safe and clean!", "🔮")),
        Pair("metal_miner", Triple("Can Crusher 🥫", "Recycled 8 aluminum cans or metal tins!", "🥫")),
        Pair("streak_3", Triple("Super Eco Streak 🔥", "Recycled on consecutive days with an amazing eco streak!", "🔥")),
        Pair("level_3", Triple("Earth Protector 👑", "Reached Level 3 with superb active recycling habits!", "👑"))
    )

    var selectedBadgeDetail by remember { mutableStateOf<Triple<String, String, Boolean>?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "My Sticker Album 🛡️💖",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = EcoGreenDark
            )
            Text(
                text = "Earn cute custom badges for daily dedication. Can you unlock all 7 badges?",
                fontSize = 11.sp,
                color = CharcoalText.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }

        // Dashboard Stats
        Card(
            colors = CardDefaults.cardColors(containerColor = EcoGreenLight.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "⭐ Unlocked: ", fontWeight = FontWeight.Bold, color = CharcoalText, fontSize = 13.sp)
                Text(
                    text = "${earnedBadges.size} out of ${standardBadges.size} stickers!",
                    fontWeight = FontWeight.Black,
                    color = EcoGreenDark,
                    fontSize = 14.sp
                )
            }
        }

        // Sticker Grid
        val chunkedBadges = standardBadges.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            chunkedBadges.forEach { rowList ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowList.forEach { pair ->
                        val badgeId = "badge_" + pair.first
                        val detail = pair.second
                        val earnedMatch = earnedBadges.find { it.badgeId == badgeId }
                        val isUnlocked = earnedMatch != null

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) KidsWhite else KidsWhite.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = if (isUnlocked) 2.dp else 1.dp,
                                    color = if (isUnlocked) SolarYellowTertiary else SoftGrayOutline.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable {
                                    selectedBadgeDetail = Triple(detail.first, detail.second, isUnlocked)
                                }
                                .testTag("badge_card_$badgeId")
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth()
                                    .alpha(if (isUnlocked) 1f else 0.45f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(
                                            if (isUnlocked) EcoGreenLight else SoftGrayOutline.copy(alpha = 0.2f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isUnlocked) {
                                        Text(text = detail.third, fontSize = 34.sp)
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Hidden Sticker",
                                            tint = CharcoalText.copy(alpha = 0.6f),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = detail.first,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = CharcoalText,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (isUnlocked) "UNLOCKED! 🏆" else "Locked Sticker 🔒",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) EcoGreenDark else CharcoalText.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    // Pad extra space if odd number
                    if (rowList.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    // Detail Badge sticker inspector popover
    selectedBadgeDetail?.let { triple ->
        Dialog(onDismissRequest = { selectedBadgeDetail = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = KidsWhite),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, SolarYellowTertiary, RoundedCornerShape(32.dp))
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (triple.third) "💖 TROPHY STICKER UNLOCKED!" else "🔒 HOW TO UNLOCK THIS BADGE",
                        fontWeight = FontWeight.Bold,
                        color = if (triple.third) CoralOrange else CharcoalText.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = triple.first,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = EcoGreenDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = triple.second,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = CharcoalText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { selectedBadgeDetail = null },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(140.dp)
                    ) {
                        Text("GREAT! 👍", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// CUSTOM OVERLAYS, DIALOGS, & MISC COMPOSE
// ==========================================

@Composable
fun CelebrationDialog(
    itemType: String,
    quantity: Int,
    weightSaved: Double,
    onDismiss: () -> Unit
) {
    val (title, emoji, background, comparisonText) = when (itemType.lowercase()) {
        "paper" -> Quadruple(
            "Forest Guardian! 🌳", "📚", EcoGreenPrimary,
            "That saves approximately ${String.format(Locale.getDefault(), "%.1f", quantity * 0.05)}% of a full tall green pine tree!"
        )
        "plastic" -> Quadruple(
            "Ocean Protector! 🌊", "🥤", SkyBlueSecondary,
            "That keeps plastic away from cute turtles and feeds energy to a television for ${quantity * 1} hours! 📺"
        )
        "glass" -> Quadruple(
            "Sparkling Wizard! ✨", "🔮", SolarYellowTertiary,
            "That is enough energy recycling to power a computer for ${quantity * 2} hours! 💻"
        )
        "metal" -> Quadruple(
            "Can Crusher Legend! 🥫", "🥫", CoralOrange,
            "That saves aluminum to build super rockets or powers a game console for ${quantity * 3} hours! 🎮"
        )
        else -> Quadruple("Super Eco Hero!", "🚀", EcoGreenPrimary, "Wow! What an absolute recycling savior deed of kindness!")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = KidsWhite),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, SolarYellowTertiary, RoundedCornerShape(32.dp))
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Happy emoji
                Text(text = "🎉🎈♻️", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(background.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = EcoGreenDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Congratulations! You recycled $quantity item(s) and kept ${String.format(Locale.getDefault(), "%.2f", weightSaved)} kg of waste completely out of landfills! 🥳",
                    fontSize = 13.sp,
                    color = CharcoalText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Custom educational text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EcoGreenLight, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = comparisonText,
                        fontSize = 11.sp,
                        color = EcoGreenDark,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("YES! I'M AN ECO HERO! 🏆", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileCustomizationDialog(
    currentName: String,
    currentAvatar: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var selectedAvatar by remember { mutableStateOf(currentAvatar) }

    val mascots = listOf("🦊", "🦁", "🐨", "🐸", "🐼", "🦖", "🦄", "🦥", "🦉", "🐧")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = KidsWhite),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, EcoGreenPrimary, RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Your Recycler Hero Identity! 🎨",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = EcoGreenDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Large preview of selected hero
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(EcoGreenLight, CircleShape)
                        .border(1.dp, EcoGreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = selectedAvatar, fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name Textfield input
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { if (it.length <= 15) nameInput = it },
                    label = { Text("Hero's Name", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoGreenPrimary,
                        focusedLabelColor = EcoGreenDark
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Choose Your Recycler Companion Avatar:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = CharcoalText.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mascots selector list
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(mascots) { mascot ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (selectedAvatar == mascot) EcoGreenLight else SoftGrayOutline.copy(alpha = 0.15f),
                                    CircleShape
                                )
                                .border(
                                    width = if (selectedAvatar == mascot) 2.dp else 0.dp,
                                    color = EcoGreenDark,
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatar = mascot }
                                .testTag("mascot_option_$mascot"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = mascot, fontSize = 28.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { onSave(nameInput.ifBlank { "Eco Kid" }, selectedAvatar) },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1.2f).testTag("profile_save_button")
                    ) {
                        Text("Save Hero! 🌟", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Pair & Quadruple helper models
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
