package com.augniture.beta.ui.general.productos

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.augniture.beta.R
import com.augniture.beta.domain.Producto
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.product_cardview_item_single.view.*

class ProductosAdapter(
    private val itemRecyclerViewResourceId: Int,
    private val productos: MutableList<Producto> = mutableListOf(),
    private val glide: RequestManager,
    private val itemClickListener: ItemClickListener<Producto>
) : RecyclerView.Adapter<ProductosAdapter.ProductosViewHolder>() {

    class ProductosViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val previewProductoIV: ImageView = view.productItemPreviewImageCV
        val titleProductoTV: TextView = view.productItemTitleCV

        fun bindItemClickListener(
            producto: Producto,
            itemClickListener: ItemClickListener<Producto>
        ) {
            itemView.setOnClickListener {
                itemClickListener.onItemClicked(producto)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosViewHolder {
        return ProductosViewHolder(
            LayoutInflater.from(parent.context).inflate(itemRecyclerViewResourceId, parent, false)
        )
    }

    override fun getItemCount(): Int = productos.size

    override fun onBindViewHolder(holder: ProductosViewHolder, position: Int) {
        var currentProducto = productos[position]

        glide.load(currentProducto.imagen)
            .error(glide.load(R.drawable.preview_missing))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(holder.previewProductoIV)

        holder.titleProductoTV.text = currentProducto.nombre

        // Setup Item Click Listener
        holder.bindItemClickListener(currentProducto, itemClickListener)
    }

    fun update(newProductos: List<Producto>) {
        productos.clear()
        productos.addAll(newProductos)

        notifyDataSetChanged()
    }

}