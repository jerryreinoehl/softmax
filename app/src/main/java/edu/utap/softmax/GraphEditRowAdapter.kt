package edu.utap.softmax

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.softmax.databinding.RowGraphEditBinding

class GraphEditRowAdapter(
    private val onClickListener: (SoftmaxClient.Run, Boolean) -> Unit
) : ListAdapter<SoftmaxClient.Run, GraphEditRowAdapter.ViewHolder>(GraphEditDiff()) {

    class GraphEditDiff : DiffUtil.ItemCallback<SoftmaxClient.Run>() {
        override fun areItemsTheSame(
            oldItem: SoftmaxClient.Run,
            newItem: SoftmaxClient.Run
        ): Boolean {
            return oldItem.runId == newItem.runId
        }

        override fun areContentsTheSame(
            oldItem: SoftmaxClient.Run,
            newItem: SoftmaxClient.Run
        ): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }
    }

    inner class ViewHolder(private val rowBinding: RowGraphEditBinding)
    : RecyclerView.ViewHolder(rowBinding.root) {
        private lateinit var run: SoftmaxClient.Run

        init {
            rowBinding.graphSwitch.setOnClickListener {
                onClickListener(run, rowBinding.graphSwitch.isChecked)
            }
        }

        fun bind(run: SoftmaxClient.Run) {
            this.run = run
            rowBinding.runNumTv.text = adapterPosition.toString()
            rowBinding.timestampTv.text = run.timestamp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : GraphEditRowAdapter.ViewHolder {
        val rowBinding = RowGraphEditBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: GraphEditRowAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}