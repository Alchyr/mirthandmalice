package mirthandmalice.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MathHelper {
    public static float dist(float x1, float x2, float y1, float y2)
    {
        return (float) Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }

    public static float angle(float x1, float x2, float y1, float y2)
    {
        return MathUtils.atan2(y2 - y1, x2 - x1) * 180.0f / MathUtils.PI;
    }

    public static Vector2[] getCirclePoints(float cX, float cY, float distance, int num)
    {
        return getCirclePoints(cX, cY, distance, num, 0);
    }

    public static Vector2[] getCirclePoints(float cX, float cY, float distance, int num, float startAngle)
    {
        Vector2[] result = new Vector2[num];

        float change = 360.0f / num;
        for (int i = 0; i < num; ++i)
        {
            result[i] = new Vector2(cX + MathUtils.cosDeg(startAngle) * distance, cY + MathUtils.sinDeg(startAngle) * distance);
            startAngle += change;
        }

        return result;
    }
}
