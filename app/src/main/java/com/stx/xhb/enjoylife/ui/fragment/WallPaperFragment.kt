package com.stx.xhb.enjoylife.ui.fragment

import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.stx.xhb.core.base.BaseFragment
import com.stx.xhb.core.utils.ScreenUtil
import com.stx.xhb.core.widget.DividerDecoration
import com.stx.xhb.enjoylife.R
import com.stx.xhb.enjoylife.data.entity.Feed
import com.stx.xhb.enjoylife.mvp.contract.GetWallPaperContract
import com.stx.xhb.enjoylife.mvp.presenter.GetWallPaperPresenter
import com.stx.xhb.enjoylife.ui.activity.PhotoViewActivity
import com.stx.xhb.enjoylife.ui.activity.WallPagerActivity
import com.stx.xhb.enjoylife.ui.adapter.TuChongWallPaperAdapter
import java.util.*

/**
 * @author: xiaohaibin.
 * @time: 2018/6/29
 * @mail:xhb_199409@163.com
 * @github:https://github.com/xiaohaibin
 * @describe: 图虫壁纸
 */
class WallPaperFragment : BaseFragment(), GetWallPaperContract.View, SwipeRefreshLayout.OnRefreshListener {

    var getWallPaperPresenter: GetWallPaperPresenter? = null
    private var mRvTuChong: RecyclerView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var page = 1
    private var mTuChongListAdapter: TuChongWallPaperAdapter? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_common
    }

    override fun initView() {
        mRvTuChong = getView(R.id.recly_view)
        mSwipeRefreshLayout = getView(R.id.refresh_layout)
        val gridLayoutManager = GridLayoutManager(activity, 3);
        mSwipeRefreshLayout?.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimary)
        mRvTuChong?.layoutManager = gridLayoutManager
        mRvTuChong?.addItemDecoration(DividerDecoration(ContextCompat.getColor(activity, R.color.colorWhite), ScreenUtil.dp2px(activity, 1)))
        mTuChongListAdapter = TuChongWallPaperAdapter(R.layout.list_item_wall_paper)
        mTuChongListAdapter?.openLoadAnimation()
        mRvTuChong?.adapter = mTuChongListAdapter
    }

    override fun initData() {
        getWallPaperPresenter = GetWallPaperPresenter(this)
    }

    override fun setListener() {
        mTuChongListAdapter?.setOnImageItemClickListener(object : TuChongWallPaperAdapter.OnImageItemClickListener {
            override fun setOnImageClick(view: View, imageList: ArrayList<String>) {
                val intent = Intent(mContext, WallPagerActivity::class.java)
                intent.putStringArrayListExtra("image", imageList)
                intent.putExtra("pos", 0)
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, view, WallPagerActivity.TRANSIT_PIC)
                try {
                    ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle())
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    startActivity(intent)
                }

            }
        })
        mSwipeRefreshLayout?.setOnRefreshListener(this)
        mTuChongListAdapter?.setOnLoadMoreListener({
            page++
            getWallPaperPresenter?.getWallPaper(page)
        }, mRvTuChong)
    }

    override fun onResponse(feedList: List<Feed>, isMore: Boolean) {
        onLoadComplete(page)
        mTuChongListAdapter?.setEnableLoadMore(isMore)
        for (i in feedList.indices) {
            val feedListBean = feedList[i]
            if ("post" == feedListBean.type) {
                mTuChongListAdapter?.addData(feedListBean)
            }
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun onVisible() {
        super.onVisible()
        mSwipeRefreshLayout?.setRefreshing(true)
        onRefresh()
    }

    override fun showMsg(msg: String) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        page = 1
        getWallPaperPresenter?.getWallPaper(page)
    }

    override fun onDestroy() {
        super.onDestroy()
        getWallPaperPresenter?.destory()
    }

    private fun onLoadComplete(page: Int) {
        if (page == 1) {
            mTuChongListAdapter?.setNewData(null)
            mSwipeRefreshLayout?.setRefreshing(false)
        } else {
            mTuChongListAdapter?.loadMoreComplete()
        }
    }
}