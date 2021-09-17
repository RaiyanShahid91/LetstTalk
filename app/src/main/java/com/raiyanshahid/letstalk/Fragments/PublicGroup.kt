package com.raiyanshahid.letstalk.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.raiyanshahid.letstalk.R
import com.raiyanshahid.letstalk.databinding.FragmentPublicGroupBinding


class PublicGroup : Fragment() {

    lateinit var binding : FragmentPublicGroupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPublicGroupBinding.inflate(layoutInflater,container,false)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.framelayoutgroup, GroupFragments()).commit()

        binding.group.setOnClickListener {
            binding.groupView.setBackgroundResource(R.color.darkblue)
            binding.groupText.setTextColor(Color.parseColor("#060C2C"))
            binding.publicgroupView.setBackgroundResource(R.color.darkgrey)
            binding.publicgroupText.setTextColor(Color.parseColor("#8A8888"))
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.framelayoutgroup, GroupFragments()).commit()

        }

        binding.publicgroup.setOnClickListener {
            binding.publicgroupView.setBackgroundResource(R.color.darkblue)
            binding.publicgroupText.setTextColor(Color.parseColor("#060C2C"))
            binding.groupView.setBackgroundResource(R.color.darkgrey)
            binding.groupText.setTextColor(Color.parseColor("#8A8888"))
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.framelayoutgroup, PublicGroupFragments()).commit()
        }

        return  binding.root

    }
}