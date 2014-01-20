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

For more information about the team and the framework 
please refer to the [website](http://dexter.isti.cnr.it).

A simple demo of the system is running at this 
[address](http://vinello.isti.cnr.it:8080/). The tagger used in the demo 
is our implemented version of [TAGME](http://tagme.di.unipi.it), please 
note that some annotations could be different since the two frameworks use
 different Wikipedia dumps and different methods for extracting the spots.

We are currently working on improving the quality of the code, that we 
plan to publicly release (under [Apache License V2](http://www.apache.org/licenses/LICENSE-2.0.html)) 
in the mid of September (but if you can't wait send us an [email](http://dexter.isti.cnr.it/contact) 
and we will give you access to our internal repo).

In the meanwhile, you can [download the binary jar](http://dexter.isti.cnr.it/download) 
containing all the resources for running Dexter.

### Updates
  - 19 November: added support (and REST API) for categories, more details on the [website](www.dxtr.it/dev)



[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/diegoceccarelli/dexter/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

