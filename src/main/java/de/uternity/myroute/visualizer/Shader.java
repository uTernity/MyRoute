package de.uternity.myroute.visualizer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

public class Shader
{
	public final int vertexShaderId, fragmentShaderId, geometryShaderId, programShaderId;

	public Shader(String vertexShaderPath, String geometryShaderPath, String fragmentShaderPath)
	{
		vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
		geometryShaderId = glCreateShader(GL_GEOMETRY_SHADER);
		fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
		programShaderId = glCreateProgram();

		try
		{
			glShaderSource(vertexShaderId, getShaderCode(vertexShaderPath));
		}

		catch (URISyntaxException | IOException e)
		{
			e.printStackTrace();
		}
	}

	private String getShaderCode(String fileName) throws URISyntaxException, IOException
	{
		return new String(Files.readAllBytes(Paths.get(Main.class.getClassLoader().getResource(fileName).toURI())));
	}
}