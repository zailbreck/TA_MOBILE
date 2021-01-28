package com.kaorimaps.ssa.menu

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kaorimaps.ssa.R
import com.kaorimaps.ssa.config.InfoListAdapter
import com.kaorimaps.ssa.config.InfoLog
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_news.view.*
import java.util.concurrent.Executors


class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        activity?.title = arguments?.getString("fragmentName")
        view.setBackgroundColor(Color.WHITE)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListView()
    }

    private fun getListView(){
        val _title = ArrayList<String>()
        val _content = ArrayList<String>()

        val executors = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executors.execute{
            if(arguments?.getString("errResult") == "DataFailed"){
                progress_bar.visibility = View.GONE
                description.text = "Gagal Mengambil Data!\n" +
                        "Server Pemkomedan Offline"
            }else if(arguments?.getString("errResult") == "RTO"){
                progress_bar.visibility = View.GONE
                description.text = "Request Time Out!"
            }else {
                arguments?.getParcelableArrayList<InfoLog>("InfoList")?.forEach { it ->
                    _title.add(it.title.toString())
                    _content.add(it.content.toString())
                }
                Handler(Looper.getMainLooper()).post {
                    loading.visibility = View.GONE
                    Toast.makeText(context, "Data Berhasil DiReload", Toast.LENGTH_SHORT).show()
                }
            }
            handler.post{
                val myListAdapter = InfoListAdapter(requireActivity(), _title, _content)
                list_view.adapter = myListAdapter
            }
        }
    }
}