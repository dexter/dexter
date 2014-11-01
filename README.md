
![Dexter](http://dexter.isti.cnr.it/static/images/dexter.png "Dexter")

The entity linking (aka Wikification) task aims 
at identifying all the small text fragments in a document referring 
to an entity contained in a given knowledge base, e.g., Wikipedia. 
The annotation is usually organized in three tasks. Given an input 
document the first task consists in discovering the fragments that could 
refer to an entity. Since a mention could refer to multiple entities, 
it is necessary to perform a disambiguation step, where the correct entity 
is selected among the candidates. Finally, discovered entities are ranked 
by some measure of relevance. Many entity linking algorithms have been proposed, 
but unfortunately only a few authors have released the source code or some APIs. 
As a result, evaluating today the performance of a method on a single subtask, 
or comparing different techniques is difficult.

For these reasons we implemented Dexter, a framework that implements 
some popular algorithms and provides all the tools needed to develop 
any entity linking technique. We believe that a shared framework is 
fundamental to perform fair comparisons and improve the state of the art.

For more information about the team and the framework.
please refer to the [website](http://dexter.isti.cnr.it).

A simple demo is also available on the website. The tagger used in the demo 
is our implemented version of [TAGME](http://tagme.di.unipi.it), please 
note that some annotations could be different since the two frameworks use
different Wikipedia dumps and different methods for extracting the spots.

# Ok, but I don't have time to understand how things _really_ work, I just want to use it

Hiha! just download the model with the binaries (model was generated from the [English Wikipedia dump 07/07/2014](http://dumps.wikimedia.org/enwiki/20140707/enwiki-20140707-pages-articles.xml.bz2])):

    wget http://hpc.isti.cnr.it/~ceccarelli/dexter2.tar.gz
	tar -xvzf dexter2.tar.gz
	cd dexter2
	java -Xmx4000m -jar dexter-2.1.0.jar


And then visit on your browser the address [http://localhost:8080/dexter-webapp/dev]().
It will show the available REST-API.  Enjoy! 

# Cool, I want to know more!



# Developing

You can use Dexter in two different ways: 

  * Using the Rest API, after downloading the jar and its resources;
  * Using the Java API;
  * Jsonp API + JQuery plugin;
  * Python Client.
 
## Start a REST Server 

### Download the Resources

Click on this [http://hpc.isti.cnr.it/~ceccarelli/dexter2.tar.gz](link) for downloading Dexter. 

The archive requires around 2 Gigabytes, and contains the 
Dexter binary code (''dexter2.1.0.jar'') and the 
model used by Dexter for annotating.

The current model is generated from the 07/07/2014 English 
Wikipedia dump, available [http://dumps.wikimedia.org/enwiki/20140707/enwiki-20140707-pages-articles.xml.bz2](here). 
(we plan to release updated models for English and other languages). 

Once the download is finished, untar the package, and from the directory ''dexter'', just run

  	java -Xmx3000m -jar dexter2.1.0.jar

(you will need at least 3G of ram and Java 7).
The framework should be available in few seconds at the address:

  	http://localhost:8080/
	
The REST-api is available at: 
	
	http://localhost:8080/dexter-webapp/dev

First query will take a bit because Dexter will have to load all the model in main
memory. 

	
## Configuring Dexter

Dexter 2 is configured through an XML file `dexter-conf.xml`. 
Don't worry, is not to hard to understand ;), by default Dexter 
searches for the configuration file in the root directory. 
   
### Set the Dexter model

In the beginning of the file:

	<models>
		<default>en</default>
		<model>
			<name>en</name>
			<path>FIXME</path>
		</model>
		<model>
			<name>it</name>
			<path>data/it</path>
		</model>
	</models>

replace the path in `FIXME` with the absolute or relative path to the folder that contains the dexter model. If you download 
the model from the website, the folder is called data. Once you setup the folder just start the server running the command: 

    
### Use the client

Once you performed the installation, you will have to add to your maven 
project the dependency:

 	 <dependency>
 	 	<groupId>it.cnr.isti.hpc</groupId>
  		<artifactId>dexter-client</artifactId>
  		<version>2.1.0</version>
  	</dependency>

Then will be able to call the REST api from your have project 
using the DexterRestClient as in the following example: 

  	DexterRestClient client = new DexterRestClient("http://localhost:8080/dexter-webapp/api/rest");
 	AnnotatedDocument ad = client
  		.annotate("Dexter is an American television drama series which debuted on Showtime on October 1, 2006. The series 	centers on Dexter Morgan (Michael C. Hall), a blood spatter pattern analyst for the fictional Miami Metro Police Department 	(based on the real life Miami-Dade Police Department) who also leads a secret life as a serial killer. Set in Miami, the show's first season was largely based on the novel Darkly Dreaming Dexter, the first of the Dexter series novels by Jeff Lindsay. It was adapted for television by screenwriter James Manos, Jr., who wrote the first episode. ");
 	 
 	System.out.println(ad);
  	
  	SpottedDocument sd = client
  		.spot("Dexter is an American television drama series which debuted on Showtime on October 1, 2006. The series centers on Dexter Morgan (Michael C. Hall), a blood spatter pattern analyst for the fictional Miami Metro Police Department (based on the real life Miami-Dade Police Department) who also leads a secret life as a serial killer. Set in Miami, the show's first season was largely based on the novel Darkly Dreaming Dexter, the first of the Dexter series novels by Jeff Lindsay. It was adapted for television by screenwriter James Manos, Jr., who wrote the first episode. ");
    
    System.out.println(sd);
  	
    ArticleDescription desc = client.getDesc(5981816);
  
    System.out.println(desc);
  
If you have installed the code with `mvn install`, and run the server on your machine, you can create an instance of the client with the this address

    DexterRestClient client = new DexterRestClient(
  		  "http://localhost:8080/dexter-webapp/api/rest");
 
   
### More details about the configuration file

The configuration file mainly contains details on the paths of the model files, and it allows to register plugin and to configure the tagger. 

More in detail, it allows to register: 

#### Spotters

The component that detects mentions in the text, and maps each mention to a list of candidate entities, you can register a spotter writing inside the `<spotters>`tag:
  
 ```
 	<spotters>
		<default>wiki-dictionary</default>
		<spotter>
			<name>wiki-dictionary</name>
			<class>it.cnr.isti.hpc.dexter.spotter.DictionarySpotter</class>
		</spotter>
	</spotters>
 ```

In this example, the standard dictionary spotter is registered with the name `wiki-dictionary` and set as default dictionary, you can also register other dictionaries, for example: 

 ```
 	<spotters>
		<default>wiki-dictionary</default>
		<spotter>
			<name>wiki-dictionary</name>
			<class>it.cnr.isti.hpc.dexter.spotter.DictionarySpotter</class>
		</spotter>
			<spotter>
			<name>my-dictionary</name>
			<class>com.mycompany.Myspotter</class>
		</spotter>
	</spotters>
 ```

Here we registered a new spotter with the name `my-dictionary`. You can create a new spotter extending the abstract class `
AbstractSpotter` and then adding it to the classpath of the dexter webapp (you can put the jar in the lib folder, or more easy, install the jar with maven and add the dependency in the `dexter-webapp/pom.xml`). 

At runtime, the rest functions allow to specify the spotter that you want to use with the parameter `spt`. If you don't specify the name of the spotter, the `default` is used. 

#### SpotFilters 
A spot filter allows to filter the mentions produced by the Spotter before they are sent to the Disambiguator. For example, if you would like to filter short spots, or spots with low probability etc etc, still you can write your filters or use the filters provided by Dexter. As for the spotter, filters must be registered and than you can use them in a spotter. 
For example: 

```
	<spotFilters>
		<spotFilter>
			<name>probability-filter</name>
			<class>it.cnr.isti.hpc.dexter.spotter.filter.SpotProbabilityFilter</class>
			<params>
				<param>
					<name>lp</name>
					<value>0.02</value>
				</param>
			</params>
		</spotFilter> 
    </spotFilters>
```

Registers a filter that removes mentions with low probability to be links to entities (note the parameter lp). 
You could register two different filters: 

```
	<spotFilters>
		<spotFilter>
			<name>f0.02</name>
			<class>it.cnr.isti.hpc.dexter.spotter.filter.SpotProbabilityFilter</class>
			<params>
				<param>
					<name>lp</name>
					<value>0.02</value>
				</param>
			</params>
		</spotFilter> 
		<spotFilter>
			<name>f0.5</name>
			<class>it.cnr.isti.hpc.dexter.spotter.filter.SpotProbabilityFilter</class>
			<params>
				<param>
					<name>lp</name>
					<value>0.5</value>
				</param>
			</params>
		</spotFilter> 
    </spotFilters>
```
Here the first filter `f0.02` removes all the spots with link probability lower than 0.02, whil the second `f0.5` removes all the spots with link probability lower than 0.5. 

Than you can set several spot filters with a spotter: 

```
	<spotter>
			<name>wiki-dictionary</name>
			<class>it.cnr.isti.hpc.dexter.spotter.DictionarySpotter</class>
			<filters>
				<filter>
					<name>probability-filter</name>
				</filter>
				<filter>
					<name>overlaps-filter</name>
				</filter>
			</filters>
	</spotter>
```

The `overlaps-filter` removes spots that overlap in the text. Filters are applied in the order they appear in the configuration. 

#### Disambiguation functions

As for the spotters, you can register a disambiguation function and call it from the rest interface. 

		<disambiguator>
			<name>tagme</name>
			<class>it.cnr.isti.hpc.tagme.Tagme</class>
			<params>
				<param>
					<name>window-size</name>
					<value>30</value>
				</param>
				<param>
					<name>epsilon</name>
					<value>0.7</value>
				</param>
			</params>
		</disambiguator>
		
Tagme and Wikiminer are provided as a dependency, in dexter-code you can find a disambiguator that always select the most probable entity for a mention. 

If you want to add a disambiguator, just implement the interface `Disambiguator` (in `dexter-core`).

The interface has two methods: 
	
	public EntityMatchList disambiguate(DexterLocalParams localParams,
			SpotMatchList sml);
	
	public void init(DexterParams dexterParams,
			DexterLocalParams dexterModuleParams);
		

Init will be called just once when the disambiguator object is created, if there are params in the disambiguator snippet
(as for tagme) this params will be passed in the dexterModuleParams variable. At run time, when you annotate a document, the parameter that you put in the post/get query will be pushed in the localParams object, so you can play with the parameters 
of you disambiguator. This works also for spotter and spot filters. 


