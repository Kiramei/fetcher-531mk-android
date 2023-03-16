package com.kiramei.kikacomic.ui.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.kiramei.kikacomic.R;
import com.kiramei.kikacomic.data.Config;
import com.kiramei.kikacomic.data.Service;
import com.kiramei.kikacomic.request.InformationGetter;
import com.kiramei.kikacomic.request.WebsiteChooser;
import com.kiramei.kikacomic.ui.intent.DetailPanel;
import com.yalantis.taurus.PullToRefreshView;


public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    Context context;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return R.layout.homeview;
            case 1:
                return R.layout.sitesview;
            case 2:
                return R.layout.exploreview;
            case 3:
                return R.layout.starsview;
            default:
                return R.layout.settingview;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType, parent, false);
        ViewHolder holder;
        switch (viewType) {
            case R.layout.homeview:
                holder = new HomeHolder(view);
                break;
            case R.layout.sitesview:
                holder = new SitesHolder(view);
                break;
            case R.layout.exploreview:
                holder = new ExploreHolder(view);
                break;
            case R.layout.starsview:
                holder = new FavHolder(view);
                break;

            default:
                holder = new SettingHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 5;
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

    static class HomeHolder extends ViewHolder {
        protected int res;

        private String[] img, href, title;

        private static final String DEFAULT_URL = "http://m.mxsan.com";

        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            final GridView manga = itemView.findViewById(R.id.manga);
            final EditText search = itemView.findViewById(R.id.searchBox);
            final PullToRefreshView refresh = itemView.findViewById(R.id.refresh);

            refresh.setOnRefreshListener(() -> getRandomPage(itemView, manga, refresh));

            getRandomPage(itemView, manga, refresh);

            search.setOnEditorActionListener((v, actionId, event) -> actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED);

            //getMainPage(itemView, manga, refresh);

            search.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ((InputMethodManager) (itemView.getContext()).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    refresh.setRefreshing(true);
                    new Thread(() -> {
                        final InformationGetter informationGetter = new InformationGetter().search(search.getText().toString());
                        itemView.post(() -> manga.setAdapter(new LoadingAdapter(itemView.getContext(), informationGetter.getImg(), informationGetter.getHref(), informationGetter.getTitle())));
                        refresh.setRefreshing(false);
                    }).start();
                }
                return true;
            });
        }


        @SuppressWarnings("unused")
        private void getMainPage(@NonNull View itemView, GridView manga, PullToRefreshView refresh) {
            new Thread(() -> {
                refresh.setRefreshing(true);
                InformationGetter j = InformationGetter.push().add(DEFAULT_URL);
                img = j.getImg();
                href = j.getHref();
                title = j.getTitle();
                itemView.post(() -> manga.setAdapter(new LoadingAdapter(itemView.getContext(), img, href, title)));
                refresh.setRefreshing(false);
            }).start();
        }
    }

    private static void getRandomPage(@NonNull View itemView, GridView manga, PullToRefreshView refresh) {
        refresh.setRefreshing(true);
        new Thread(() -> {
            String[] img = new String[Config.RANDOM_MAX], href = new String[Config.RANDOM_MAX], title = new String[Config.RANDOM_MAX];
            for (int i = 0; i < Config.RANDOM_MAX; i++) {
                href[i] = "http://m.mxsan.com/lifan/"
                        + (((int) (Math.random() * (Service.mxsan_max - Service.MXSAN_MIN))) + Service.MXSAN_MIN)
                        + ".html";
                WebsiteChooser w = new WebsiteChooser(href[i]);
                img[i] = w.getHref();
                String temp = w.getTitle();
                if (temp == null) temp = "";
                title[i] = temp.length() > Config.TITLE_LENGTH_MAX ?
                        temp.substring(0, Config.TITLE_LENGTH_MAX) : temp;
            }
            itemView.post(() -> {
                manga.setAdapter(new LoadingAdapter(itemView.getContext(), img, href, title));
                refresh.setRefreshing(false);
            });
        }).start();
    }

    static class SitesHolder extends ViewHolder {
        public SitesHolder(@NonNull View itemView) {
            super(itemView);
            final GridView sites = itemView.findViewById(R.id.sites);
            sites.setAdapter(new IndexAdapter(itemView.getContext()));
        }
    }

    class ExploreHolder extends ViewHolder {
        String[] img = new String[Config.EXPLORE_MAX];
        String[] href = new String[Config.EXPLORE_MAX];
        String[] title = new String[Config.EXPLORE_MAX];

        public ExploreHolder(@NonNull View itemView) {
            super(itemView);
            final ViewPager2 explorer = itemView.findViewById(R.id.explorer);
            final ProgressBar progressBar = itemView.findViewById(R.id.progress);
            new Thread(() -> {
                for (int i = 0; i < Config.EXPLORE_MAX; i++) {
                    href[i] = "http://m.mxsan.com/lifan/"
                            + (((int) (Math.random() * (Service.mxsan_max - Service.MXSAN_MIN))) + Service.MXSAN_MIN)
                            + ".html";
                    WebsiteChooser w = new WebsiteChooser(href[i]);
                    img[i] = w.getHref();
                    String temp = w.getTitle();
                    if (temp == null) temp = "";
                    title[i] = temp.length() > Config.TITLE_LENGTH_MAX ?
                            temp.substring(0, Config.TITLE_LENGTH_MAX) : temp;
                }
                RecyclerView.Adapter<ViewHolder> v = new RecyclerView.Adapter<ViewHolder>() {
                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        LayoutInflater inflater = LayoutInflater.from(context);
                        View view = inflater.inflate(R.layout.manga, parent, false);
                        return new ImageHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
                        Glide.with(context).load(img[position])
                                .thumbnail(Glide.with(context).load(R.drawable.load))
                                .apply(new RequestOptions().fitCenter())
                                .transition(withCrossFade())
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(Config.ROUNDING_RADIUS)))
                                .into(((ImageHolder) holder).i);
                        ((ImageHolder) holder).u.setText(title[position]);
                        ((ImageHolder) holder).i.setOnClickListener(v -> {
                            Intent i = new Intent();
                            i.putExtra("origin", href[position]);
                            i.setClass(context, DetailPanel.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        });
                    }

                    @Override
                    public int getItemCount() {
                        return Config.EXPLORE_MAX;
                    }

                    class ImageHolder extends ViewHolder {
                        final ImageView i;
                        final TextView u;

                        public ImageHolder(@NonNull View itemView) {
                            super(itemView);
                            i = itemView.findViewById(R.id.vim);
                            u = itemView.findViewById(R.id.title_u);
                        }
                    }
                };
                itemView.post(() -> {
                    explorer.setAdapter(v);
                    progressBar.setVisibility(View.INVISIBLE);
                });

            }).start();

        }
    }

    static class FavHolder extends ViewHolder {
        public FavHolder(@NonNull View itemView) {
            super(itemView);
            pull(itemView);
            PullToRefreshView sf = itemView.findViewById(R.id.star_fresh);
            sf.setOnRefreshListener(() -> {
                pull(itemView);
                sf.setRefreshing(false);
            });
        }

        private void pull(@NonNull View itemView) {
            GridView fav = itemView.findViewById(R.id.likes);
            if (Service.stars.equals("")) return;
            String[] groups = Service.stars.split("~`~");
            String[] href = new String[groups.length];
            String[] images = new String[groups.length];
            String[] title = new String[groups.length];
            for (int i = 0; i < groups.length; i++) {
                String[] group = groups[i].split(",");
                href[i] = group[0];
                String temp = group[1];
                if (temp == null) temp = "";
                title[i] = temp.length() > Config.TITLE_LENGTH_MAX ?
                        temp.substring(0, Config.TITLE_LENGTH_MAX) : temp;
                images[i] = group[2];
            }
            fav.setAdapter(new LoadingAdapter(itemView.getContext(), images, href, title));
        }
    }

    static class SettingHolder extends ViewHolder {
        public SettingHolder(@NonNull View itemView) {
            super(itemView);
            final EditText msgbox = itemView.findViewById(R.id.msgbox);
            final Button backup = itemView.findViewById(R.id.backup);
            final Button copy = itemView.findViewById(R.id.paste);
            final Button recover = itemView.findViewById(R.id.recover);

            backup.setOnClickListener(v -> msgbox.setText(Service.getStarEncryption()));
            copy.setOnClickListener(v -> {
                ((ClipboardManager) itemView.getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE))
                        .setPrimaryClip(ClipData.newPlainText("newText", msgbox.getText()));
                Toast.makeText(itemView.getContext(), R.string.copy, Toast.LENGTH_SHORT).show();
            });
            recover.setOnClickListener(v -> Service.saveWholeStar(msgbox.getText().toString(), itemView.getContext()));

        }
    }
}
