package com.jokerscalperpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF030B10)
private val Panel = Color(0xFF071820)
private val Neon = Color(0xFF00E5D4)
private val Muted = Color(0xFF8AA7AA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { JokerApp() }
    }
}

@Composable fun JokerApp() {
    var tab by remember { mutableIntStateOf(0) }
    var broker by remember { mutableStateOf("MetaTrader 5") }
    var server by remember { mutableStateOf("") }
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var symbol by remember { mutableStateOf("XAUUSD") }
    var lot by remember { mutableStateOf("0.01") }
    var execution by remember { mutableStateOf("BOTH") }
    var trades by remember { mutableStateOf("5") }
    var api by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("NOT CONNECTED") }
    var armed by remember { mutableStateOf(false) }
    MaterialTheme(colorScheme = darkColorScheme(primary=Neon, background=Bg, surface=Panel)) {
        Scaffold(containerColor=Bg, bottomBar={
            NavigationBar(containerColor=Color(0xFF06151B), contentColor=Neon) {
                listOf("Home","Scanner","Settings").forEachIndexed { i,s ->
                    NavigationBarItem(selected=tab==i,onClick={tab=i},
                        icon={ Icon(when(i){0->Icons.Default.Home;1->Icons.Default.ShowChart;else->Icons.Default.Settings},s) },
                        label={Text(s)}, colors=NavigationBarItemDefaults.colors(selectedIconColor=Neon,selectedTextColor=Neon,indicatorColor=Color(0xFF123B40),unselectedTextColor=Muted))
                }
            }
        }) { pad ->
            Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(18.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Text("◉",color=Neon,fontSize=28.sp); Spacer(Modifier.width(10.dp))
                    Column { Text("JOKER SCALPER PRO",color=Neon,fontWeight=FontWeight.Black,letterSpacing=2.sp); Text("AI TRADING CONTROL CENTER",color=Muted,fontSize=10.sp) }
                    Spacer(Modifier.weight(1f)); StatusPill(status)
                }
                Spacer(Modifier.height(18.dp))
                when(tab) {
                    0 -> {
                        HeroPanel()
                        Spacer(Modifier.height(14.dp))
                        Section("BROKER CONNECTION")
                        OutlinedTextField(broker,{broker=it},label={Text("Broker platform")},modifier=Modifier.fillMaxWidth())
                        OutlinedTextField(server,{server=it},label={Text("MT5 server")},modifier=Modifier.fillMaxWidth())
                        OutlinedTextField(account,{account=it},label={Text("Account / login")},modifier=Modifier.fillMaxWidth(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))
                        OutlinedTextField(password,{password=it},label={Text("Trading password (not stored)")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth())
                        Button(onClick={status="BRIDGE NOT CONFIGURED"; password=""},modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Neon,contentColor=Bg)) { Text("CHECK CONNECTION") }
                        Spacer(Modifier.height(16.dp)); Section("TRADE CONFIGURATION")
                        OutlinedTextField(symbol,{symbol=it},label={Text("Broker symbol (e.g. XAUUSD)")},modifier=Modifier.fillMaxWidth())
                        OutlinedTextField(lot,{lot=it},label={Text("Lot size")},modifier=Modifier.fillMaxWidth(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal))
                        OutlinedTextField(trades,{trades=it},label={Text("Maximum simultaneous trades")},modifier=Modifier.fillMaxWidth(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))
                        Text("Execution direction",color=Muted,modifier=Modifier.padding(top=10.dp))
                        Row { listOf("BUY","SELL","BOTH").forEach { FilterChip(selected=execution==it,onClick={execution=it},label={Text(it)},modifier=Modifier.padding(end=6.dp)) } }
                        OutlinedTextField(api,{api=it},label={Text("Secure trading bridge URL (HTTPS)")},modifier=Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment=Alignment.CenterVertically) { Checkbox(armed,{armed=it}); Text("I understand live orders can lose money",color=Muted,fontSize=12.sp) }
                        Button(onClick={status=if(armed) "BRIDGE REQUIRED" else "NOT ARMED"},enabled=armed,modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Neon,contentColor=Bg)) { Text("ARM LIVE TRADING (LOCKED)") }
                        Text("Live execution is disabled until a verified HTTPS bridge and MT5 Expert Advisor are installed and tested.",color=Color(0xFFFFC66D),fontSize=11.sp)
                    }
                    1 -> {
                        Section("CORTEX • CHART SCANNER")
                        HeroPanel()
                        InfoCard("SCANNER STATUS","Awaiting market feed / MT5 bridge")
                        InfoCard("SIGNAL ENGINE","No signal generated. Connect a verified data feed.")
                        InfoCard("RISK CONTROL","Set max trades, lot size and broker-side stop loss before enabling execution.")
                        Button(onClick={status="DATA FEED REQUIRED"},modifier=Modifier.fillMaxWidth()) { Text("SCAN MARKET") }
                    }
                    else -> {
                        Section("SYSTEM SETTINGS")
                        InfoCard("BROKER","$broker")
                        InfoCard("SYMBOL","$symbol • Lot $lot • $execution")
                        InfoCard("CONNECTION","$status")
                        InfoCard("SECURITY","Credentials are not persisted by this starter app. Use broker-authorized OAuth/API or a private bridge; never embed passwords or API secrets in the APK.")
                    }
                }
                Spacer(Modifier.height(22.dp))
                Text("JOKER SCALPER PRO • ANDROID STARTER",color=Muted,fontSize=10.sp)
            }
        }
    }
}
@Composable fun HeroPanel() {
    Column(Modifier.fillMaxWidth().background(Panel,RoundedCornerShape(22.dp)).border(1.dp,Neon.copy(alpha=.45f),RoundedCornerShape(22.dp)).padding(18.dp)) {
        Text("JOKER",color=Neon,fontSize=12.sp,letterSpacing=5.sp)
        Text("SCALPER PRO",color=Color.White,fontSize=27.sp,fontWeight=FontWeight.Black)
        Text("Precision starts with controlled risk.",color=Muted,fontSize=12.sp)
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Metric("MODE","LIVE • LOCKED"); Metric("SYMBOL","XAUUSD"); Metric("FEED","OFFLINE")
        }
    }
}
@Composable fun Metric(k:String,v:String) { Column { Text(k,color=Muted,fontSize=9.sp); Text(v,color=Neon,fontSize=11.sp,fontWeight=FontWeight.Bold) } }
@Composable fun Section(t:String) { Text(t,color=Neon,fontSize=12.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp,modifier=Modifier.padding(vertical=10.dp)) }
@Composable fun InfoCard(k:String,v:String) { Column(Modifier.fillMaxWidth().padding(vertical=5.dp).background(Panel,RoundedCornerShape(14.dp)).padding(14.dp)) { Text(k,color=Neon,fontSize=10.sp); Text(v,color=Color.White,fontSize=14.sp) } }
@Composable fun StatusPill(s:String) { Text(s,color=if(s=="NOT CONNECTED") Color(0xFFFFC66D) else Neon,fontSize=9.sp,modifier=Modifier.border(1.dp,Neon.copy(alpha=.5f),RoundedCornerShape(20.dp)).padding(8.dp)) }
