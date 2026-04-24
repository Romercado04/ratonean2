package com.example.ratonean2_app.shoppinglist.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ratonean2_app.shoppinglist.domain.model.ShoppingItem
import com.example.ratonean2_app.shoppinglist.presentation.ShoppingIntent

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    onIntent: (ShoppingIntent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onIntent(ShoppingIntent.ToggleItem(item.id, it)) }
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    text = item.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                )
                Text(text = item.brand)
                Text(
                    text = "$${String.format("%.2f", item.price)} c/u",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Controles de cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onIntent(ShoppingIntent.UpdateQuantity(item.id, item.quantity - 1)) }) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                }
                Text(text = "${item.quantity}")
                IconButton(onClick = { onIntent(ShoppingIntent.UpdateQuantity(item.id, item.quantity + 1)) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }
}