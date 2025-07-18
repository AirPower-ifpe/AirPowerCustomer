package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.repository.model.HomeScreenAlarmSummaryCard
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.view.ui.theme.tb_secondary_light
import java.util.UUID


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.

@Composable
fun AlarmCardInfo(
    alarmCardInfo: HomeScreenAlarmSummaryCard,
    onClick: (severity: String) -> Unit,
    backgroundColor: Color = Color.White
) {
    CustomCard(
        modifier = Modifier
            .clip(RoundedCornerShape(cardCornerRadius))
            .fillMaxWidth()
            .height(120.dp)
            .background(backgroundColor)
            .clickable { onClick(alarmCardInfo.severity) },
        layouts = listOf {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomColumn(
                        modifier = Modifier.wrapContentSize(),
                        layouts = listOf {
                            CustomText(
                                text = alarmCardInfo.severity,
                                alignment = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = tb_primary_light,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    )
                }

                Spacer(Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomColumn(
                        modifier = Modifier.wrapContentSize(),
                        layouts = listOf {
                            CustomText(
                                text = alarmCardInfo.occurrence.toString(),
                                alignment = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = tb_secondary_light,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    )
}


@Composable
fun CardInfo(
    label: String,
    value: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color.White
) {
    CustomCard(
        modifier = Modifier
            .clip(RoundedCornerShape(cardCornerRadius))
            .fillMaxWidth()
            .height(120.dp)
            .background(backgroundColor)
            .clickable { onClick() },
        layouts = listOf {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomColumn(
                        modifier = Modifier.wrapContentSize(),
                        layouts = listOf {
                            CustomText(
                                text = label,
                                alignment = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = tb_primary_light,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    )
                }

                Spacer(Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomColumn(
                        modifier = Modifier.wrapContentSize(),
                        layouts = listOf {
                            CustomText(
                                text = value,
                                alignment = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = tb_secondary_light,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    )
}