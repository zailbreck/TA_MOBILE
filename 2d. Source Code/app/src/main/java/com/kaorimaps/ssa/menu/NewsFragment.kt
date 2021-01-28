package com.kaorimaps.ssa.menu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.kaorimaps.ssa.config.NewsListAdapter
import com.kaorimaps.ssa.config.NewsLog
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_news.view.*
import kotlinx.android.synthetic.main.news_item.view.*
import java.net.URL
import java.util.concurrent.Executors


class NewsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_news, container, false)
        activity?.title = arguments?.getString("fragmentName")
        view.setBackgroundColor(Color.WHITE)

        view.list_view.setOnItemClickListener { parent, _, position, _ ->
            val title = parent.getItemAtPosition(position)
            val sum = parent.summary.text
            Toast.makeText(context, "$title \n\n $sum", Toast.LENGTH_LONG).show()
        }


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListView()
    }

    private fun getListView(){
        val _title = ArrayList<String>()
        val _tgl = ArrayList<String>()
        val _summ = ArrayList<String>()
        val _img = ArrayList<Bitmap>()

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
                arguments?.getParcelableArrayList<NewsLog>("NewsList")?.forEach { it ->
                    _title.add(it.title.toString())
                    _tgl.add(it.date.toString())
                    _summ.add(it.summary.toString())
                    _img.add(url2bit(it.img.toString()))
                }
                Handler(Looper.getMainLooper()).post {
                    loading.visibility = View.GONE
                    Toast.makeText(context, "Data Berhasil Dimuat", Toast.LENGTH_SHORT).show()
                }
            }
            handler.post{
                val myListAdapter = NewsListAdapter(requireActivity(), _title, _tgl, _summ, _img)
                list_view.adapter = myListAdapter
            }
        }

    }



    private fun url2bit(str: String): Bitmap{
        return BitmapFactory.decodeStream(URL(str).openConnection().getInputStream())
    }
}
