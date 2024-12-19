/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import meteordevelopment.meteorclient.mixininterface.ICapabilityTracker;
import meteordevelopment.meteorclient.utils.PreInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.opengl.GL32C.*;

public class GL {
    private static final FloatBuffer MAT = BufferUtils.createFloatBuffer(4 * 4);

    private static final ICapabilityTracker DEPTH = getTracker("DEPTH");
    private static final ICapabilityTracker BLEND = getTracker("BLEND");
    private static final ICapabilityTracker CULL = getTracker("CULL");
    private static final ICapabilityTracker SCISSOR = getTracker("SCISSOR");

    private static boolean depthSaved, blendSaved, cullSaved, scissorSaved;

    private static boolean changeBufferRenderer = true;

    public static int CURRENT_IBO;
    private static int prevIbo;

    private GL() {
    }

    @PreInit
    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("canvas")) changeBufferRenderer = false;
    }

    // Generation

    public static int genBuffer() {
        return GlStateManager.genBuffers();
    }

    public static int genTexture() {
        return GlStateManager.genTextures();
    }

    public static int genFramebuffer() {
        return GlStateManager.genFramebuffers();
    }

    // Deletion

    public static void deleteBuffer(int buffer) {
        GlStateManager.deleteBuffers(buffer);
    }

    public static void deleteShader(int shader) {
        GlStateManager.deleteShader(shader);
    }

    public static void deleteTexture(int id) {
        GlStateManager.deleteTexture(id);
    }

    public static void deleteFramebuffer(int fbo) {
        GlStateManager.deleteFramebuffers(fbo);
    }

    public static void deleteProgram(int program) {
        GlStateManager.deleteProgram(program);
    }

    // Binding

    public static void bindVertexBuffer(int vbo) {
        GlStateManager.bindBuffers(GL_ARRAY_BUFFER, vbo);
    }

    public static void bindIndexBuffer(int ibo) {
        if (ibo != 0) prevIbo = CURRENT_IBO;
        GlStateManager.bindBuffers(GL_ELEMENT_ARRAY_BUFFER, ibo != 0 ? ibo : prevIbo);
    }

    public static void bindFramebuffer(int fbo) {
        GlStateManager.bindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    // Buffers

    public static void bufferData(int target, ByteBuffer data, int usage) {
        GlStateManager.bufferData(target, data, usage);
    }

    public static void drawElements(int mode, int first, int type) {
        GlStateManager.drawArrays(mode, first, type);
    }

    // Vertex attributes

    public static void enableVertexAttribute(int i) {
        GlStateManager.enableVertexAttribArray(i);
    }

    public static void vertexAttribute(int index, int size, int type, boolean normalized, int stride, long pointer) {
        GlStateManager.vertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    // Shaders

    public static int createShader(int type) {
        return GlStateManager.createShader(type);
    }

    public static void shaderSource(int shader, CharSequence source) {
        GlStateManager.shaderSource(shader, source);
    }

    public static String compileShader(int shader) {
        GlStateManager.compileShader(shader);

        if (GlStateManager.getShader(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            return GlStateManager.getShaderInfoLog(shader, 512);
        }

        return null;
    }

    public static int createProgram() {
        return GlStateManager.createProgram();
    }

    public static String linkProgram(int program, int vertShader, int fragShader) {
        GlStateManager.attachShader(program, vertShader);
        GlStateManager.attachShader(program, fragShader);
        GlStateManager.linkProgram(program);

        if (GlStateManager.getProgram(program, GL_LINK_STATUS) == GL_FALSE) {
            return GlStateManager.getShaderInfoLog(program, 512);
        }

        return null;
    }

    public static void useProgram(int program) {
        GlStateManager.useProgram(program);
    }

    public static void viewport(int x, int y, int width, int height) {
        GlStateManager.viewport(x, y, width, height);
    }

    // Uniforms

    public static int getUniformLocation(int program, String name) {
        return GlStateManager.getUniformLocation(program, name);
    }

    public static void uniformInt(int location, int v) {
        GlStateManager.uniform1(location, v);
    }

    public static void uniformFloat(int location, float v) {
        glUniform1f(location, v);
    }

    public static void uniformFloat2(int location, float v1, float v2) {
        glUniform2f(location, v1, v2);
    }

    public static void uniformFloat3(int location, float v1, float v2, float v3) {
        glUniform3f(location, v1, v2, v3);
    }

    public static void uniformFloat4(int location, float v1, float v2, float v3, float v4) {
        glUniform4f(location, v1, v2, v3, v4);
    }

    public static void uniformFloat3Array(int location, float[] v) {
        glUniform3fv(location, v);
    }

    public static void uniformMatrix(int location, Matrix4f v) {
        v.get(MAT);
        GlStateManager.uniformMatrix4(location, false, MAT);
    }

    // Textures

    public static void pixelStore(int name, int param) {
        GlStateManager.pixelStore(name, param);
    }

    public static void textureParam(int target, int name, int param) {
        GlStateManager.texParameter(target, name, param);
    }

    public static void textureImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    public static void defaultPixelStore() {
        pixelStore(GL_UNPACK_SWAP_BYTES, GL_FALSE);
        pixelStore(GL_UNPACK_LSB_FIRST, GL_FALSE);
        pixelStore(GL_UNPACK_ROW_LENGTH, 0);
        pixelStore(GL_UNPACK_IMAGE_HEIGHT, 0);
        pixelStore(GL_UNPACK_SKIP_ROWS, 0);
        pixelStore(GL_UNPACK_SKIP_PIXELS, 0);
        pixelStore(GL_UNPACK_SKIP_IMAGES, 0);
        pixelStore(GL_UNPACK_ALIGNMENT, 4);
    }

    public static void generateMipmap(int target) {
        glGenerateMipmap(target);
    }

    // Framebuffers

    public static void framebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        GlStateManager.framebufferTexture2D(target, attachment, textureTarget, texture, level);
    }

    public static void clear(int mask) {
        GlStateManager.clearColor(0, 0, 0, 1);
        GlStateManager.clear(mask,false);
    }

    // State

    public static void saveState() {
        depthSaved = DEPTH.get();
        blendSaved = BLEND.get();
        cullSaved = CULL.get();
        scissorSaved = SCISSOR.get();
    }

    public static void restoreState() {
        DEPTH.set(depthSaved);
        BLEND.set(blendSaved);
        CULL.set(cullSaved);
        SCISSOR.set(scissorSaved);

        disableLineSmooth();
    }

    public static void enableDepth() {
        GlStateManager.enableDepthTest();
    }
    public static void disableDepth() {
        GlStateManager.disableDepthTest();
    }

    public static void enableBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    public static void disableBlend() {
        GlStateManager.disableBlend();
    }

    public static void enableCull() {
        GlStateManager.enableCull();
    }
    public static void disableCull() {
        GlStateManager.disableCull();
    }

    public static void enableLineSmooth() {
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);
    }
    public static void disableLineSmooth() {
        glDisable(GL_LINE_SMOOTH);
    }

    public static void bindTexture(Identifier id) {
        GlStateManager.activeTexture(GL_TEXTURE0);
        mc.getTextureManager().bindTexture(id);
    }

    public static void bindTexture(int i, int slot) {
        GlStateManager.activeTexture(GL_TEXTURE0 + slot);
        GlStateManager.bindTexture(i);
    }
    public static void bindTexture(int i) {
        bindTexture(i, 0);
    }

    public static void resetTextureSlot() {
        GlStateManager.activeTexture(GL_TEXTURE0);
    }

    private static ICapabilityTracker getTracker(String fieldName) {
        try {
            Class<?> glStateManager = GlStateManager.class;

            Field field = glStateManager.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object state = field.get(null);

            String trackerName = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "com.mojang.blaze3d.platform.GlStateManager$class_1018");

            Field capStateField = null;
            for (Field f : state.getClass().getDeclaredFields()) {
                if (f.getType().getName().equals(trackerName)) {
                    capStateField = f;
                    break;
                }
            }

            capStateField.setAccessible(true);
            return (ICapabilityTracker) capStateField.get(state);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Could not find GL state tracker '" + fieldName + "'", e);
        }
    }
}
