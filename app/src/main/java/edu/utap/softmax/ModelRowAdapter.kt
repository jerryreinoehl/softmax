package edu.utap.softmax

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.softmax.databinding.RowModelBinding

class ModelRowAdapter : ListAdapter<SoftmaxClient.Model, ModelRowAdapter.ViewHolder>(ModelDiff()) {

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
        fun bind(model: SoftmaxClient.Model) {
            rowBinding.name.text = model.name

            rowBinding.root.setOnClickListener {
                println(model.modelId)
            }
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