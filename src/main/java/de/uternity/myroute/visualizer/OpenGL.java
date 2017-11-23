package de.uternity.myroute.visualizer;

import de.uternity.myroute.creator.classes.Highway;
import de.uternity.myroute.creator.classes.Node;
import de.uternity.myroute.creator.streams.HighwayReader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGL
{
	private boolean polygonMode;
	private int shaderProgramId, vaoId;
	private long windowId;
	private int indicesCount;

	float xx = 0, yy = 0, speed = 0.01f, scale = 1;

	List<Float> vals = new ArrayList<>();

	public void init() throws Exception
	{
		initGLFW();
		initOpenGL();
		meshSetup();
		//textureSetup();
		shaderSetup();

		while (!glfwWindowShouldClose(windowId)) // as long as GLFW hasn't been told to close
		{
			glClearColor(0f, 0f, 0f, 1f); // set the screen color with rgba value
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color buffer

			render();

			glfwSwapBuffers(windowId); // swap the color buffer
			glfwPollEvents(); // check if any events are triggered, update window state, call functions
		}

		cleanUp();
	}

	private void initGLFW() throws Exception
	{
		if (!glfwInit()) // initialize GLFW
			throw new Exception();

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // specify major OpenGL version
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3); // specify minor OpenGL version
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // get access to a smaller subset of OpenGL features

		windowId = glfwCreateWindow(800, 600, "OpenGL Test", NULL, NULL); // create new window with given width, height and title

		if (windowId == 0) // if window creation failed
		{
			glfwTerminate(); // terminate GLFW
			throw new Exception();
		}

		glfwSwapInterval(1); // set vertical synchronization
		glfwMakeContextCurrent(windowId); // make context of window the main context of current thread
		glfwSetWindowSizeCallback(windowId, (l, i, i1) ->
		{
			glViewport(0, 0, i, i1); // tell OpenGL size of rendering window (first two parameters lower left location, width and height of GLFW window)
		});

		glfwSetKeyCallback(windowId, (l, i, i1, i2, i3) ->
		{
			if (i == GLFW_KEY_P && i2 == GLFW_PRESS)
			{
				if (polygonMode)
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				else
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

				polygonMode = !polygonMode;
			}

			if (i == GLFW_KEY_ESCAPE && i2 == GLFW_PRESS)
				glfwSetWindowShouldClose(windowId, true); // tell GLFW to close the window
		});
	}

	private void initOpenGL()
	{
		GL.createCapabilities(); // create capabilities
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		glViewport(0, 0, 800, 600); // tell OpenGL size of rendering window (first two parameters lower left location, width and height of GLFW window)
	}

	private void meshSetup() throws IOException
	{
		HighwayReader highways = new HighwayReader("51;7.utr");
		Iterator<Highway> iterator = highways.iterator();

		List<Highway> streets = new ArrayList<>();

		while (iterator.hasNext())
		{
			Highway next = iterator.next();

			streets.add(next);
		}


		List<Integer> indicesVals = new ArrayList<>();
		int maxIndices = streets.stream().mapToInt((h)->h.getWayNodes().size()).sum();
		int k = 0;
		for(Highway highway : streets)
		{
			for(int j = 0; j < highway.getWayNodes().size(); j++)
			{
				vals.add((float)highway.getWayNodes().get(j).getLon());
				vals.add((float)highway.getWayNodes().get(j).getLat());
				vals.add(0.0f);
				if(new String(highway.getTags(),"ISO-8859-1").matches("(.*highway=footway.*)|(.*highway=path.*)|(.*highway=pedestrian.*)"))
					vals.add(0.0f);
				else if(new String(highway.getTags(),"ISO-8859-1").matches("(.*highway=motorway.*)|(.*highway=trunk.*)|(.*highway=primary.*)"))
					vals.add(2.0f);
				else
					vals.add(1.0f);
				
				if(j != highway.getWayNodes().size()-1)
				{
					indicesVals.add(Math.max(0, k-1));
					indicesVals.add(k);
					indicesVals.add(Math.min(maxIndices-1, k+1));
					indicesVals.add(Math.min(maxIndices-1, k+2));
					k++;
				}
			}
			k++;
		}

		xx = -vals.get(0);
		yy = -vals.get(1);
		xx = -7.567f;
		yy = -51.0f;

		float[] v = new float[vals.size()];
		for(int i = 0; i < vals.size(); i++)
			v[i] = vals.get(i);

		int[] indices = new int[indicesVals.size()];
		for(int i = 0; i < indicesVals.size(); i++)
			indices[i] = indicesVals.get(i);
		indicesCount = indicesVals.size();

		int vboId = glGenBuffers(); // create VBO (vertex buffer object) to send multiple vertex data to the graphics card at once
		vaoId = glGenVertexArrays(); // create VAO (vertex array object)

		glBindVertexArray(vaoId); // bind VAO
		glBindBuffer(GL_ARRAY_BUFFER, vboId); // bind array buffer to VBO
		glBufferData(GL_ARRAY_BUFFER, v, GL_STATIC_DRAW); // copy data to buffer

		int eboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId); // bind element array buffer to EBO
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW); // copy data to buffer

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * Float.BYTES, 0 * Float.BYTES); // tell OpenGL how to interpret vertex data
		glEnableVertexAttribArray(0); // enable vertex attributes
		glVertexAttribPointer(1, 1, GL_FLOAT, false, 4 * Float.BYTES, 3 * Float.BYTES); // tell OpenGL how to interpret vertex data
		glEnableVertexAttribArray(1); // enable vertex attributes
	}

	private void textureSetup()
	{
		//textureId = glGenTextures();

		try
		{
			TextureLoader textureLoader = new TextureLoader();
			TextureLoader.Image image = textureLoader.loadTexture("texture.jpg");

			//glBindTexture(GL_TEXTURE_2D, textureId);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.data);
			glGenerateMipmap(GL_TEXTURE_2D);
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void shaderSetup() throws IOException, URISyntaxException
	{
		int vertexShaderId = glCreateShader(GL_VERTEX_SHADER); // create a shader object
		glShaderSource(vertexShaderId, getShaderCode("shader/vertexShader.glsl")); // attach shader source code to shader object
		glCompileShader(vertexShaderId); // compile shader source code

		if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == 0)
		{
			System.err.println("Vertex shader compilation failed:\n");
			System.err.println(glGetShaderInfoLog(vertexShaderId));
			System.exit(-1);
		}
		
		int geometryShaderId = glCreateShader(GL_GEOMETRY_SHADER);
		glShaderSource(geometryShaderId, getShaderCode("shader/geometryShader.glsl"));
		glCompileShader(geometryShaderId);
		
		if (glGetShaderi(geometryShaderId, GL_COMPILE_STATUS) == 0)
		{
			System.err.println("Geometry shader compilation failed:\n");
			System.err.println(glGetShaderInfoLog(geometryShaderId));
			System.exit(-1);
		}

		int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderId, getShaderCode("shader/fragmentShader.glsl"));
		glCompileShader(fragmentShaderId);

		if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == 0)
		{
			System.err.println("Fragment shader compilation failed:\n");
			System.err.println(glGetShaderInfoLog(fragmentShaderId));
			System.exit(-1);
		}

		shaderProgramId = glCreateProgram(); // create shader program
		glAttachShader(shaderProgramId, vertexShaderId); // link vertex shader to shader program
		glAttachShader(shaderProgramId, geometryShaderId); // link shader to shader program
		glAttachShader(shaderProgramId, fragmentShaderId); // link fragment shader to shader program
		glLinkProgram(shaderProgramId); // link shader

		if (glGetProgrami(shaderProgramId, GL_LINK_STATUS) == 0)
		{
			System.err.println("Program compilation failed:\n");
			System.err.println(glGetProgramInfoLog(shaderProgramId));
			System.exit(-1);
		}

		glValidateProgram(shaderProgramId);

		if (glGetProgrami(shaderProgramId, GL_VALIDATE_STATUS) == 0)
		{
			System.err.println("Program validation failed:\n");
			System.err.println(glGetProgramInfoLog(shaderProgramId));
			System.exit(-1);
		}

		glUseProgram(shaderProgramId); // use shader program
		glDeleteShader(vertexShaderId); // delete vertex shader
		glDeleteShader(geometryShaderId);
		glDeleteShader(fragmentShaderId); // delete fragment shader

		glUniformMatrix4fv(glGetUniformLocation(shaderProgramId, "projection"), false, new Matrix4f().ortho2D(-0.01f, 0.01f, -0.01f, 0.01f).get(new float[16]));
	}

	private void render()
	{
		float time = (float) glfwGetTime();
		glfwSetTime(0);
		int vertexColorLocation = glGetUniformLocation(shaderProgramId, "vertexColor");

		if(glfwGetKey(windowId,GLFW_KEY_UP) == GLFW_PRESS)
			yy-=time*speed;
		if(glfwGetKey(windowId,GLFW_KEY_DOWN) == GLFW_PRESS)
			yy+=time*speed;
		if(glfwGetKey(windowId,GLFW_KEY_LEFT) == GLFW_PRESS)
			xx+=time*speed;
		if(glfwGetKey(windowId,GLFW_KEY_RIGHT) == GLFW_PRESS)
			xx-=time*speed;
		if(glfwGetKey(windowId,GLFW_KEY_1) == GLFW_PRESS)
			scale+=time*speed*100;
		if(glfwGetKey(windowId,GLFW_KEY_2) == GLFW_PRESS)
			scale-=time*speed*100;

		glUseProgram(shaderProgramId);
		glUniformMatrix4fv(glGetUniformLocation(shaderProgramId, "view"), false, new Matrix4f().scaling(scale).translate(xx, yy,0).get(new float[16]));
		glUniform4f(vertexColorLocation, 1.0f, 1.0f,1.0f, 1.0f);
		//glBindTexture(GL_TEXTURE_2D, textureId);
		glBindVertexArray(vaoId);
		glDrawElements(GL_LINES_ADJACENCY, indicesCount, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
		glUseProgram(0);
	}

	private void cleanUp()
	{
		glUseProgram(0);
		glDeleteProgram(shaderProgramId);
		glfwTerminate(); // terminate GLFW
	}

	private String getShaderCode(String fileName) throws URISyntaxException, IOException
	{
		return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(fileName).toURI())));
	}
}