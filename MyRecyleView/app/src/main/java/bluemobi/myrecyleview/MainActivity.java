package bluemobi.myrecyleview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {

    private SwipeRefreshLayout srLayout;
    private RecyclerView recyclerView;
    private LinearLayout ll_footer;
    private ProgressBar bottom_progressBar;
    private TextView tip_view;
    private MyAdapter myAdapter;
    private List<String> resource;
    private RelativeLayout.LayoutParams layoutParams;
    private int margin;
    private int start, count;
    private boolean loading = false, isFirst = true;
    private final static String TAG = MainActivity.class.getSimpleName();
    public final String[] imageUrls2 = {
            "http://f.hiphotos.baidu.com/image/h%3D360/sign=8d04dca1013b5bb5a1d726f806d3d523/a6efce1b9d16fdfa0a611be4b68f8c5494ee7baf.jpg",
            "http://d.hiphotos.baidu.com/image/h%3D360/sign=69856497a60f4bfb93d09852334e788f/10dfa9ec8a1363278957c93b938fa0ec08fac704.jpg",
            "http://img2.imgtn.bdimg.com/it/u=864293005,1366582771&fm=23&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1296437029,2192518954&fm=23&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1352344554,1553026899&fm=23&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2512883029,489796336&fm=23&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1204916681,2600537094&fm=23&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=2718537821,1027049906&fm=23&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1641248542,3076031739&fm=23&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=3752371578,1960096672&fm=23&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=3109072621,3566748263&fm=23&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3704778188,3261777697&fm=23&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3889451826,3273809739&fm=23&gp=0.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageLoader();
        initView();

    }

    private void initView() {
        ll_footer = (LinearLayout) findViewById(R.id.ll_footer);
        bottom_progressBar = (ProgressBar) findViewById(R.id.bottom_progressBar);
        tip_view = (TextView) findViewById(R.id.tip_view);
        srLayout = (SwipeRefreshLayout) findViewById(R.id.srlayout);
        srLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, "no new resource!", Toast.LENGTH_SHORT).show();
                        refreshData();
                        loading = false;
                        srLayout.setRefreshing(false);

                    }
                }.start();
            }
        });
        layoutParams = (RelativeLayout.LayoutParams) ll_footer.getLayoutParams();
        initData();
        initRecyclerView();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setLayoutManager(new GridLayoutManager(this,4,LinearLayoutManager.VERTICAL,false));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(myAdapter = new MyAdapter());
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    Log.e(TAG, "SCROLL_STATE_IDLE");
//                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                    Log.e(TAG, "SCROLL_STATE_DRAGGING");
//                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
//                    Log.e(TAG, "SCROLL_STATE_SETTLING");
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                if (loading) {
                    return;
                }
                if (resource.size() == 0) {
                    return;
                }
                int[] lastItemPosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPositions(null);
//                int lastItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//                Log.e(TAG, lastItemPosition + "");
//                if (lastItemPosition == resource.size() - 1) {
//                    margin = -ll_footer.getMeasuredHeight();
//                    loading = true;
//                    startFootAnimation(true);
//                    Toast.makeText(MainActivity.this, "loading...", Toast.LENGTH_SHORT).show();
//                    loadMore();
//                }
                for (int i : lastItemPosition) {
                    if (i == resource.size() - 1) {
                        margin = -ll_footer.getMeasuredHeight();
                        loading = true;
                        startFootAnimation(true);
                        Toast.makeText(MainActivity.this, "loading...", Toast.LENGTH_SHORT).show();
                        loadMore();
                    }
                }
            }
        });
//        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL_LIST));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerGridItemDecoration(MainActivity.this));
    }


    private void startFootAnimation(boolean in) {
        if (in) {
//            Animation footer_int = AnimationUtils.loadAnimation(MainActivity.this, R.anim.footer_in);
//            ll_footer.setVisibility(View.VISIBLE);
//            ll_footer.startAnimation(footer_int);
            ValueAnimator animator = ValueAnimator.ofInt(margin, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    layoutParams.bottomMargin = (int) animation.getAnimatedValue();
                    ll_footer.setLayoutParams(layoutParams);
                }
            });
            animator.setDuration(500);
            animator.start();
        } else {
//            Animation footer_out = AnimationUtils.loadAnimation(MainActivity.this, R.anim.footer_out);
//            ll_footer.startAnimation(footer_out);
//            ll_footer.setVisibility(View.GONE);
            ValueAnimator animator = ValueAnimator.ofInt(0, margin);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    layoutParams.bottomMargin = (int) animation.getAnimatedValue();
                    ll_footer.setLayoutParams(layoutParams);
                }
            });
            animator.setDuration(500);
            animator.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void refreshData() {
        int size = resource.size();
        resource.clear();
        myAdapter.notifyItemRangeRemoved(0, size);
        initData();
        isFirst = true;
        myAdapter.notifyItemRangeInserted(0, resource.size());
    }

    private void loadMore() {
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startFootAnimation(false);
                int adapterStart = start;
                initData();
                myAdapter.notifyItemRangeInserted(adapterStart, count);
                loading = false;
            }
        }.start();
    }

    private void initData() {
        if (resource == null) {
            resource = new ArrayList<String>();
        }
//        for (int i = 'A'; i <= 'Z'; i++) {
//            resource.add((char) i + "");
//        }
        resource.addAll(Arrays.asList(imageUrls2));
        start = resource.size() - 1;
        count = imageUrls2.length;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.recycleview_item, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            Random random = new Random();
            int height = random.nextInt() % 70 + 150;
            ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
            layoutParams.height = dip2px(MainActivity.this, height);
            ImageLoader.getInstance().displayImage(
                    resource.get(position),
                    holder.iv,
                    new DisplayImageOptions.Builder().showImageOnLoading(0).
                            showImageForEmptyUri(0).
                            showImageOnFail(0).
                            cacheInMemory(true).
                            cacheOnDisk(true).
                            considerExifParams(true).
                            build()
// ,
//                    new SimpleImageLoadingListener(){
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            ViewGroup.LayoutParams lp = holder.iv.getLayoutParams();
//                            lp.height = loadedImage.getHeight();
//                            lp.width = loadedImage.getWidth();
//                            holder.iv.setLayoutParams(lp);
//                            holder.iv.setImageBitmap(loadedImage);
//                        }
//                    }
            );
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(MainActivity.this, "delete:" + holder.tv.getText() + ",it's position:" + holder.getLayoutPosition(), Toast.LENGTH_SHORT).show();
                    resource.remove(holder.getLayoutPosition());
                    myAdapter.notifyItemRemoved(holder.getLayoutPosition());
                }
            });
        }


        @Override
        public int getItemCount() {
            return resource.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView iv;
            LinearLayout parent;

            public MyViewHolder(View itemView) {
                super(itemView);
                parent = (LinearLayout) itemView;
                iv = (ImageView) (itemView.findViewById(R.id.id_num));
            }

        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
