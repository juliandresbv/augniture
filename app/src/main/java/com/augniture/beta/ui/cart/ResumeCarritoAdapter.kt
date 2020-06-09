package com.augniture.beta.ui.cart

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.ProductoCompra
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_resume_cart_products_list.view.*
import kotlinx.android.synthetic.main.resume_cart_product_cardview_item_single.view.*

class ResumeCarritoAdapter(
    private val itemRecyclerViewResourceId: Int,
    private val productos: MutableList<ProductoCompra> = mutableListOf(),
    private val glide: RequestManager,
    private val itemClickListener: ItemClickListener<Producto>
) : RecyclerView.Adapter<ResumeCarritoAdapter.ProductosCompraViewHolder>() {

    class ProductosCompraViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val resumeTitleCartProductoTV: TextView = view.resumeCartProductItemTitleCV
        val resumePriceCartProductoTV: TextView = view.resumeCartProductItemPriceNumberCV
        val resumeQuantityCartProductoTV: TextView = view.resumeCartProductQuantityCV

        fun bindItemClickListener(producto: Producto, itemClickListener: ItemClickListener<Producto>) {
            itemView.setOnClickListener {
                itemClickListener.onItemClicked(producto)
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

        holder.resumeTitleCartProductoTV.text = currentProducto?.nombre
        holder.resumePriceCartProductoTV.text = currentProducto?.precio.toString()
        holder.resumeQuantityCartProductoTV.text = currentProductoCompra.cantidad.toString()

        // Setup Item Click Listener
        holder.bindItemClickListener(currentProducto!!, itemClickListener)
    }

    fun update(newProductos: List<ProductoCompra>) {
        productos.clear()
        productos.addAll(newProductos)

        notifyDataSetChanged()
    }

}