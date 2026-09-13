/*
 * Copyright (c) 2026 Willian Santos.
 *
 * Final Graduation Project - IFPE (Instituto Federal de Pernambuco).
 * This source code is part of a research project developed at IFPE.
 *
 * Project: AirPower Customer
 * Author: Willian Santos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifpe.edu.br.view.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class AlarmFilterTab(val label: String) {
    ALL("Todos"),
    ACTIVE("Ativos"),
    CRITICAL("Críticos"),
    CLEARED("Resolvidos")
}

@Composable
fun AlarmCenterScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val alarms by mainViewModel.getAlarmInfoSet().collectAsState()
    var selectedFilter by remember { mutableStateOf(AlarmFilterTab.ALL) }

    val filteredAlarms = remember(alarms, selectedFilter) {
        val sorted = alarms.sortedByDescending { it.createdTime }
        when (selectedFilter) {
            AlarmFilterTab.ALL -> sorted
            AlarmFilterTab.ACTIVE -> sorted.filter { !it.cleared }
            AlarmFilterTab.CRITICAL -> sorted.filter { it.severity.equals("CRITICAL", ignoreCase = true) }
            AlarmFilterTab.CLEARED -> sorted.filter { it.cleared }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Barra de Filtros Rápidos
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AlarmFilterTab.entries) { filter ->
                val count = when (filter) {
                    AlarmFilterTab.ALL -> alarms.size
                    AlarmFilterTab.ACTIVE -> alarms.count { !it.cleared }
                    AlarmFilterTab.CRITICAL -> alarms.count { it.severity.equals("CRITICAL", ignoreCase = true) }
                    AlarmFilterTab.CLEARED -> alarms.count { it.cleared }
                }

                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = "${filter.label} ($count)",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Lista de Detalhes dos Alarmes
        if (filteredAlarms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum evento localizado neste filtro",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredAlarms, key = { it.id.id }) { alarm ->
                    AlarmDetailCard(alarm = alarm)
                }
            }
        }
    }
}

@Composable
private fun AlarmDetailCard(alarm: AlarmInfo) {
    val isCleared = alarm.cleared
    val deviceName = alarm.originatorName?.takeIf { it.isNotBlank() } ?: "Medidor de Climatização"
    val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        .format(Date(alarm.createdTime))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (!isCleared) 1.2.dp else 0.5.dp,
                color = if (!isCleared) MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp)
            ),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Linha 1: Dispositivo + Severidade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                SeverityBadge(severity = alarm.severity)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Linha 2: Tipo de Anomalia
            Text(
                text = alarm.type,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Linha 3: Metadados (Status, Ciente, Timestamp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(
                        text = if (isCleared) "RESOLVIDO" else "ATIVO",
                        bgColor = if (isCleared) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        textColor = if (isCleared) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )

                    StatusBadge(
                        text = if (alarm.acknowledged) "RECONHECIDO" else "PENDENTE",
                        bgColor = if (alarm.acknowledged) Color(0xFFE3F2FD) else Color(0xFFFFF3E0),
                        textColor = if (alarm.acknowledged) Color(0xFF1565C0) else Color(0xFFE65100)
                    )
                }

                Text(
                    text = dateFormatted,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Composable
private fun SeverityBadge(severity: String) {
    val (bgColor, textColor) = when (severity.uppercase()) {
        "CRITICAL" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        "MAJOR", "HIGH" -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        "MINOR", "WARNING" -> Color(0xFFFFFDE7) to Color(0xFFF57F17)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = severity.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}