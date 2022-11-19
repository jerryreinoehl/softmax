package edu.utap.softmax

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.softmax.databinding.RowModelBinding

class ModelRowAdapter(
    private val onClickListener: (model: SoftmaxClient.Model) -> Unit
) : ListAdapter<SoftmaxClient.Model, ModelRowAdapter.ViewHolder>(ModelDiff()) {

    class ModelDiff : DiffUtil.ItemCallback<SoftmaxClient.Model>() {
        override fun areItemsTheSame(
            oldItem: SoftmaxClient.Model,
            newItem: SoftmaxClient.Model
        ): Boolean {
            return oldItem.modelId == newItem.modelId
        }

        override fun areContentsTheSame(
            oldItem: SoftmaxClient.Model,
            newItem: SoftmaxClient.Model
        ): Boolean {
            return oldItem.name == newItem.name && oldItem.runId == newItem.runId
        }
    }

    inner class ViewHolder(private val rowBinding: RowModelBinding)
    : RecyclerView.ViewHolder(rowBinding.root) {

        private lateinit var model: SoftmaxClient.Model

        init {
            rowBinding.root.setOnClickListener { view ->
                onClickListener(model)
            }
        }

        fun bind(model: SoftmaxClient.Model) {
            this.model = model
            rowBinding.name.text = model.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : ModelRowAdapter.ViewHolder {
        val rowBinding = RowModelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ModelRowAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}