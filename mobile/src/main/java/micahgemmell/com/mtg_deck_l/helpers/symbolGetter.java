package micahgemmell.com.mtg_deck_l.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.loopj.android.http.BinaryHttpResponseHandler;
import com.squareup.picasso.Picasso;

/**
 * Created by sonicemerald on 10/6/14.
 */
public class symbolGetter {
    Bitmap mBitmap;
//
//
//    public Bitmap getManaCostSymbol(String mc) {
//        String url;
//
//        if (mc.equalsIgnoreCase("w")) {
//            url = "http://mtgimage.com/";
//            getImageFrom("white", "m");
//        }
//    }
}
//				else if (mc.equalsIgnoreCase("u")) {
//					d = resources.getDrawable(R.drawable.glyph_u);
//				}
//				else if (source.equalsIgnoreCase("b")) {
//					d = resources.getDrawable(R.drawable.glyph_b);
//				}
//				else if (source.equalsIgnoreCase("r")) {
//					d = resources.getDrawable(R.drawable.glyph_r);
//				}
//				else if (source.equalsIgnoreCase("g")) {
//					d = resources.getDrawable(R.drawable.glyph_g);
//				}
//				else if (source.equalsIgnoreCase("t")) {
//					d = resources.getDrawable(R.drawable.glyph_tap);
//				}
//				else if (source.equalsIgnoreCase("q")) {
//					d = resources.getDrawable(R.drawable.glyph_untap);
//				}
//				else if (source.equalsIgnoreCase("wu") || source.equalsIgnoreCase("uw")) {
//					d = resources.getDrawable(R.drawable.glyph_wu);
//				}
//				else if (source.equalsIgnoreCase("ub") || source.equalsIgnoreCase("bu")) {
//					d = resources.getDrawable(R.drawable.glyph_ub);
//				}
//				else if (source.equalsIgnoreCase("br") || source.equalsIgnoreCase("rb")) {
//					d = resources.getDrawable(R.drawable.glyph_br);
//				}
//				else if (source.equalsIgnoreCase("rg") || source.equalsIgnoreCase("gr")) {
//					d = resources.getDrawable(R.drawable.glyph_rg);
//				}
//				else if (source.equalsIgnoreCase("gw") || source.equalsIgnoreCase("wg")) {
//					d = resources.getDrawable(R.drawable.glyph_gw);
//				}
//				else if (source.equalsIgnoreCase("wb") || source.equalsIgnoreCase("bw")) {
//					d = resources.getDrawable(R.drawable.glyph_wb);
//				}
//				else if (source.equalsIgnoreCase("bg") || source.equalsIgnoreCase("gb")) {
//					d = resources.getDrawable(R.drawable.glyph_bg);
//				}
//				else if (source.equalsIgnoreCase("gu") || source.equalsIgnoreCase("ug")) {
//					d = resources.getDrawable(R.drawable.glyph_gu);
//				}
//				else if (source.equalsIgnoreCase("ur") || source.equalsIgnoreCase("ru")) {
//					d = resources.getDrawable(R.drawable.glyph_ur);
//				}
//				else if (source.equalsIgnoreCase("rw") || source.equalsIgnoreCase("wr")) {
//					d = resources.getDrawable(R.drawable.glyph_rw);
//				}
//				else if (source.equalsIgnoreCase("2w") || source.equalsIgnoreCase("w2")) {
//					d = resources.getDrawable(R.drawable.glyph_w2);
//				}
//				else if (source.equalsIgnoreCase("2u") || source.equalsIgnoreCase("u2")) {
//					d = resources.getDrawable(R.drawable.glyph_u2);
//				}
//				else if (source.equalsIgnoreCase("2b") || source.equalsIgnoreCase("b2")) {
//					d = resources.getDrawable(R.drawable.glyph_b2);
//				}
//				else if (source.equalsIgnoreCase("2r") || source.equalsIgnoreCase("r2")) {
//					d = resources.getDrawable(R.drawable.glyph_r2);
//				}
//				else if (source.equalsIgnoreCase("2g") || source.equalsIgnoreCase("g2")) {
//					d = resources.getDrawable(R.drawable.glyph_g2);
//				}
//				else if (source.equalsIgnoreCase("s")) {
//					d = resources.getDrawable(R.drawable.glyph_s);
//				}
//				else if (source.equalsIgnoreCase("pw") || source.equalsIgnoreCase("wp")) {
//					d = resources.getDrawable(R.drawable.glyph_pw);
//				}
//				else if (source.equalsIgnoreCase("pu") || source.equalsIgnoreCase("up")) {
//					d = resources.getDrawable(R.drawable.glyph_pu);
//				}
//				else if (source.equalsIgnoreCase("pb") || source.equalsIgnoreCase("bp")) {
//					d = resources.getDrawable(R.drawable.glyph_pb);
//				}
//				else if (source.equalsIgnoreCase("pr") || source.equalsIgnoreCase("rp")) {
//					d = resources.getDrawable(R.drawable.glyph_pr);
//				}
//				else if (source.equalsIgnoreCase("pg") || source.equalsIgnoreCase("gp")) {
//					d = resources.getDrawable(R.drawable.glyph_pg);
//				}
//				else if (source.equalsIgnoreCase("p")) {
//					d = resources.getDrawable(R.drawable.glyph_p);
//				}
//				else if (source.equalsIgnoreCase("+oo")) {
//					d = resources.getDrawable(R.drawable.glyph_inf);
//				}
//				else if (source.equalsIgnoreCase("100")) {
//					d = resources.getDrawable(R.drawable.glyph_100);
//				}
//				else if (source.equalsIgnoreCase("1000000")) {
//					d = resources.getDrawable(R.drawable.glyph_1000000);
//				}
//				else if (source.equalsIgnoreCase("hr") || source.equalsIgnoreCase("rh")) {
//					d = resources.getDrawable(R.drawable.glyph_hr);
//				}
//				else if (source.equalsIgnoreCase("hw") || source.equalsIgnoreCase("wh")) {
//					d = resources.getDrawable(R.drawable.glyph_hw);
//				}
//				else if (source.equalsIgnoreCase("c")) {
//					d = resources.getDrawable(R.drawable.glyph_c);
//				}
//				else if (source.equalsIgnoreCase("z")) {
//					d = resources.getDrawable(R.drawable.glyph_z);
//				}
//				else if (source.equalsIgnoreCase("y")) {
//					d = resources.getDrawable(R.drawable.glyph_y);
//				}
//				else if (source.equalsIgnoreCase("x")) {
//					d = resources.getDrawable(R.drawable.glyph_x);
//				}
//				else if (source.equalsIgnoreCase("h")) {
//					d = resources.getDrawable(R.drawable.glyph_half);
//				}
//				else if (source.equalsIgnoreCase("pwk")) {
//					d = resources.getDrawable(R.drawable.glyph_pwk);
//				}
//    * */