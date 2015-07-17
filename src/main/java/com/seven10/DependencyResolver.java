package com.seven10;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import java.io.File;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo( name = "instdep" )
public class DependencyResolver extends AbstractMojo
{
    /**
     * Location of the file.
     */
    @Parameter( property = "instdep.path", defaultValue = "${project.build.directory}/lib" )
    private File outputDirectory;
    
    @Parameter( property = "instdep.platform", defaultValue="win64")
    private String platform;
    
    @Parameter( property = "instdep.centera-version", defaultValue="3.3.718")
    private String centeraVersion;
    
    @Parameter( property = "instdep.dependency-name", defaultValue="centera-sdk")
    private String baseName;
    
    @Parameter( property = "instdep.dependency-group", defaultValue="com.seven10")
    private String dependencyGroupId;
    

    @Parameter( defaultValue="${project}", readonly=true, required=true)
    private MavenProject mavenProject;

    @Parameter( defaultValue="${session}", readonly=true, required=true)
    private MavenSession mavenSession;

    @Component( role = BuildPluginManager.class )
    private BuildPluginManager pluginManager;
    
    private Element createArtifactItems()
    {
    	return element(name("artifactItems"),
    			element(name("artifactItem"),
					element(name("groupId"), dependencyGroupId),
					element(name("artifactId"), baseName),
					element(name("version"), centeraVersion ),
					element(name("classifier"), platform),
					element(name("type"), "jar"),
					element(name("overWrite"), "true"),
					element(name("outputDirectory"), outputDirectory.getAbsolutePath()))); 
    }
 
    public void execute()  throws MojoExecutionException
    {
    	getLog().info( String.format( "--- %s:%s:%s:%s -> %s (call within prepare-dependencies) @ empty-project ---",
    			baseName, dependencyGroupId, platform, centeraVersion, outputDirectory ) );
    	executeMojo(
    			plugin(
    					groupId("org.apache.maven.plugins"),
    					artifactId("maven-dependency-plugin"),
    					version("2.10")
    			),
    			goal("unpack"),
    			configuration(createArtifactItems()),
    			executionEnvironment(
    					mavenProject,
    					mavenSession,
    					pluginManager
    					)
    			);
    	 getLog().info( String.format( "--- %s:%s:%s:%s ended ---",
    			 baseName, dependencyGroupId, platform, centeraVersion ) );
    }
}
