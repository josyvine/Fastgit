package com.vineyard.fastgit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vineyard.fastgit.app.models.Commit
import com.vineyard.fastgit.app.models.Repository
import com.vineyard.fastgit.app.ui.theme.*
import com.vineyard.fastgit.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onCreateRepoClick: () -> Unit,
    onImportRepoClick: () -> Unit,
    onSelectRepo: (Repository) -> Unit
) {
    val recentRepos by homeViewModel.recentRepos.collectAsState()
    val recentCommits by homeViewModel.recentCommits.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GhBgDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Column {
                Text(
                    text = "FastGit Mobile Workspace",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Manage GitHub repositories without terminal Git commands",
                    fontSize = 13.sp,
                    color = GhTextSecondaryDark
                )
            }
        }

        // Quick Action Cards Grid Row
        item {
            Text(
                text = "Quick Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Create Repo",
                    subtitle = "New GitHub project",
                    icon = Icons.Default.AddCircle,
                    color = GhSuccessGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onCreateRepoClick
                )

                QuickActionCard(
                    title = "Import Repo",
                    subtitle = "From GitHub URL",
                    icon = Icons.Default.CloudDownload,
                    color = GhAccentBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onImportRepoClick
                )
            }
        }

        // Recent Repositories Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Repositories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GhAccentBlue)
                }
            }
        } else {
            items(recentRepos) { repo ->
                RepoCardItem(repo = repo, onClick = { onSelectRepo(repo) })
            }
        }

        // Recent Commits Activity Feed
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recent Commits",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        items(recentCommits) { commit ->
            CommitCardItem(commit = commit)
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GhSurfaceDark),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
            }

            Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            Text(subtitle, color = GhTextSecondaryDark, fontSize = 12.sp)
        }
    }
}

@Composable
fun RepoCardItem(repo: Repository, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GhSurfaceDark),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (repo.private) Icons.Default.Lock else Icons.Default.Folder,
                contentDescription = null,
                tint = if (repo.private) GhWarningYellow else GhAccentBlue,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repo.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                repo.description?.let { desc ->
                    Text(
                        text = desc,
                        color = GhTextSecondaryDark,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repo.language?.let { lang ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GhPrimaryViolet)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(lang, fontSize = 11.sp, color = GhTextSecondaryDark)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GhWarningYellow, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${repo.stargazersCount}", fontSize = 11.sp, color = GhTextSecondaryDark)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CallSplit, contentDescription = null, tint = GhTextSecondaryDark, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${repo.forksCount}", fontSize = 11.sp, color = GhTextSecondaryDark)
                    }
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GhTextSecondaryDark)
        }
    }
}

@Composable
fun CommitCardItem(commit: Commit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GhSurfaceDark),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Commit, contentDescription = null, tint = GhPrimaryViolet, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = commit.commit?.message ?: "Update repository content",
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${commit.commit?.author?.name ?: "FastGit"} • ${commit.sha.take(7)}",
                    fontSize = 11.sp,
                    color = GhTextSecondaryDark
                )
            }
        }
    }
}
