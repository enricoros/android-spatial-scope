
package com.enricoros.androidspatialscope;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class GLUnitCube {
    private final FloatBuffer mVertexBuffer;
    private final FloatBuffer mColorBuffer;
    private final ByteBuffer mIndexBuffer;
    private final ByteBuffer mIndexBuffer2;

    public GLUnitCube() {
        final float verticesXYZ[] = {
                -1, -1, -1, 1, -1, -1,
                1, 1, -1, -1, 1, -1,
                -1, -1, 1, 1, -1, 1,
                1, 1, 1, -1, 1, 1,
        };

        final float colorsRGBA[] = {
                0, 0, 0, 1, 1, 0, 0, 1,
                1, 1, 0, 1, 0, 1, 0, 1,
                0, 0, 1, 1, 1, 0, 1, 1,
                1, 1, 1, 1, 0, 1, 1, 1,
        };

        final byte facesTriples[] = {
                0, 4, 5, 0, 5, 1,
                1, 5, 6, 1, 6, 2,
                2, 6, 7, 2, 7, 3,
                3, 7, 4, 3, 4, 0,
                4, 7, 6, 4, 6, 5,
                3, 0, 1, 3, 1, 2
        };

        final byte cageSegmentIndices[] = {
                0, 1, 1, 2, 2, 3,
                3, 0, 4, 5, 5, 6,
                6, 7, 7, 4, 0, 4,
                1, 5, 2, 6, 3, 7
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(verticesXYZ.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(verticesXYZ);
        mVertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(colorsRGBA.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colorsRGBA);
        mColorBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(facesTriples.length);
        mIndexBuffer.put(facesTriples);
        mIndexBuffer.position(0);

        mIndexBuffer2 = ByteBuffer.allocateDirect(facesTriples.length);
        mIndexBuffer2.put(cageSegmentIndices);
        mIndexBuffer2.position(0);
    }

    public void drawGL10(GL10 gl) {
        // gl.glDisable(GL10.GL_CULL_FACE);
        // gl.glFrontFace(GL10.GL_CW);
        gl.glShadeModel(GL10.GL_SMOOTH);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColor4f(1, 1, 1, 1);
        gl.glDrawElements(GL10.GL_LINES, 24, GL10.GL_UNSIGNED_BYTE, mIndexBuffer2);
    }
}
