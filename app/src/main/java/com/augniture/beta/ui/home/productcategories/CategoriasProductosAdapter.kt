package com.augniture.beta.ui.home.featuredproducts

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.augniture.beta.R
import com.augniture.beta.ui.home.productcategories.CategoriasProductosViewModel
import com.augniture.beta.ui.supportutilities.ItemClickListener
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.categories_product_cardview_item.view.*

class CategoriasProductosAdapter(
    private val categorias: MutableList<CategoriasProductosViewModel.Categoria> = mutableListOf(),
    private val glide: RequestManager,
    private val itemClickListener: ItemClickListener<CategoriasProductosViewModel.Categoria>
) : RecyclerView.Adapter<CategoriasProductosAdapter.CategoriasProductosViewHolder>() {

    class CategoriasProductosViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val iconoCategoriaIV: ImageView = view.productCategoryItemPreviewImageCV
        val tituloCategoriaProductoTV: TextView = view.productCategoryItemTitleCV

        fun bindItemClickListener(
            categoria: CategoriasProductosViewModel.Categoria,
            itemClickListener: ItemClickListener<CategoriasProductosViewModel.Categoria>
        ) {
            itemView.setOnClickListener {
                itemClickListener.onItemClicked(categoria)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriasProductosViewHolder {
        return CategoriasProductosViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.categories_product_cardview_item, parent, false)
        )
    }

    override fun getItemCount(): Int = categorias.size

    override fun onBindViewHolder(holder: CategoriasProductosViewHolder, position: Int) {
        var currentCategoria = categorias[position]

        glide.load(currentCategoria.referenciaIcono)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(glide.load(R.drawable.preview_missing))
            .into(holder.iconoCategoriaIV)

        holder.tituloCategoriaProductoTV.text = currentCategoria.nombre

        // Setup Item Click Listener
        holder.bindItemClickListener(currentCategoria, itemClickListener)
    }

    fun update(newCategorias: List<CategoriasProductosViewModel.Categoria>) {
        categorias.clear()
        categorias.addAll(newCategorias)

        notifyDataSetChanged()
    }

}


