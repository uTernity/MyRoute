package de.uternity.myroute.visualizer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader
{
	public final int vertexShaderId, fragmentShaderId, programShaderId;

	public Shader(String vertexShaderPath, String fragmentShaderPath)
	{
		vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
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