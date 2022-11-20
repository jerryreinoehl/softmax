package edu.utap.softmax

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.softmax.databinding.RowStatListBinding

class StatListAdapter : ListAdapter<SoftmaxClient.LogItem, StatListAdapter.ViewHolder>(StatListDiff()) {

    class StatListDiff : DiffUtil.ItemCallback<SoftmaxClient.LogItem>() {
        override fun areItemsTheSame(
            oldItem: SoftmaxClient.LogItem,
            newItem: SoftmaxClient.LogItem
        ): Boolean {
            return oldItem.step == newItem.step
        }

        override fun areContentsTheSame(
            oldItem: SoftmaxClient.LogItem,
            newItem: SoftmaxClient.LogItem
        ): Boolean {
            return oldItem.data.loss == newItem.data.loss
                   && oldItem.data.accuracy == newItem.data.accuracy
        }
    }

    inner class ViewHolder(private val rowBinding: RowStatListBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(logItem: SoftmaxClient.LogItem) {
            rowBinding.stepTv.text = logItem.step.toString()
            rowBinding.lossTv.text = String.format("%.6f", logItem.data.loss)
            rowBinding.accuracyTv.text = String.format("%.4f", logItem.data.accuracy)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : StatListAdapter.ViewHolder {
        val rowBinding = RowStatListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: StatListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}