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
import com.kaorimaps.ssa.config.DestListAdapter
import com.kaorimaps.ssa.config.DestLog
import kotlinx.android.synthetic.main.dest_item.view.*
import kotlinx.android.synthetic.main.fragment_dest.*
import kotlinx.android.synthetic.main.fragment_dest.view.*
import java.net.URL
import java.util.concurrent.Executors


class DestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dest, container, false)
        activity?.title = arguments?.getString("fragmentName")
        view.setBackgroundColor(Color.WHITE)

        view.list_view.setOnItemClickListener { parent, _, position, _ ->
            val title = parent.getItemAtPosition(position)
            val sum = parent.content.text
            Toast.makeText(context, "$title \n\n $sum", Toast.LENGTH_LONG).show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListView()
    }

    private fun url2bit(str: String): Bitmap{
        return BitmapFactory.decodeStream(URL(str).openConnection().getInputStream())
    }

    private fun getListView(){
        val _title = ArrayList<String>()
        val _addr = ArrayList<String>()
        val _dist = ArrayList<String>()
        val _cont = ArrayList<String>()
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
            }else{
                arguments?.getParcelableArrayList<DestLog>("DestList")?.forEach{
                    _title.add(it.name.toString())
                    _addr.add(it.address.toString())
                    _cont.add(it.contents.toString())
                    _dist.add("""${it.distance.toString()} KM""")
                    _img.add(url2bit(it.img.toString()))
                }
                Handler(Looper.getMainLooper()).post {
                    loading.visibility = View.GONE
                    Toast.makeText(context, "Data Berhasil DiReload", Toast.LENGTH_SHORT).show()
                }
            }
            handler.post{
                val myListAdapter = DestListAdapter(requireActivity(), _title, _addr, _cont, _dist, _img)
                list_view.adapter = myListAdapter
            }
        }
    }
}
