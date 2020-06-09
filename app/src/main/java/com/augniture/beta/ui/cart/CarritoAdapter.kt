package com.augniture.beta.ui.cart

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.augniture.beta.R
import com.augniture.beta.databinding.CartProductCardviewItemSingleBinding
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.ProductoCompra
import com.augniture.beta.ui.supportutilities.AddDeleteItemClickListener
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.RequestManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.cart_product_cardview_item_single.view.*

class CarritoAdapter(
    private val itemRecyclerViewResourceId: Int,
    private val productos: MutableList<ProductoCompra> = mutableListOf(),
    private val glide: RequestManager,
    private val itemClickListener: ItemClickListener<Producto>,
    private val addDeleteItemClickListener: AddDeleteItemClickListener<Producto>
) : RecyclerView.Adapter<CarritoAdapter.ProductosCompraViewHolder>() {

    class ProductosCompraViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleCartProductoTV: TextView = view.cartProductItemTitleCV
        val priceCartProductoTV: TextView = view.cartProductItemPriceNumberCV
        val quantityCartProductoTV: TextView = view.cartProductQuantityCV

        val addProductoCompraButton: FloatingActionButton = itemView.findViewById(R.id.cartProductItemPlusFAB)
        val deleteProductoCompraButton: FloatingActionButton = itemView.findViewById(R.id.cartProductItemMinusFAB)

        fun bindItemClickListener(producto: Producto, itemClickListener: ItemClickListener<Producto>) {
            itemView.setOnClickListener {
                itemClickListener.onItemClicked(producto)
            }
        }

        fun bindAddItemClickListener(producto: Producto, addDeleteItemClickListener: AddDeleteItemClickListener<Producto>) {
            addProductoCompraButton.setOnClickListener {
                addDeleteItemClickListener.onAddButtonClicked(producto)
            }
        }

        fun bindDeleteItemClickListener(producto: Producto, addDeleteItemClickListener: AddDeleteItemClickListener<Producto>) {
            deleteProductoCompraButton.setOnClickListener {
                addDeleteItemClickListener.onDeleteButtonClicked(producto)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosCompraViewHolder {
        return ProductosCompraViewHolder(
            LayoutInflater.from(parent.context).inflate(itemRecyclerViewResourceId, parent, false)
        )
    }

    override fun getItemCount(): Int = productos.size

    override fun onBindViewHolder(holder: ProductosCompraViewHolder, position: Int) {
        var currentProductoCompra = productos[position]
        var currentProducto = currentProductoCompra.producto

        holder.titleCartProductoTV.text = currentProducto?.nombre
        holder.priceCartProductoTV.text = currentProducto?.precio.toString()
        holder.quantityCartProductoTV.text = currentProductoCompra.cantidad.toString()

        // Setup Item Click Listener
        holder.bindItemClickListener(currentProducto!!, itemClickListener)

        // Setup add and delete click listeners
        holder.bindAddItemClickListener(currentProducto!!, addDeleteItemClickListener)
        holder.bindDeleteItemClickListener(currentProducto!!, addDeleteItemClickListener)
    }

    fun update(newProductos: List<ProductoCompra>) {
        productos.clear()
        productos.addAll(newProductos)

        notifyDataSetChanged()
    }

}