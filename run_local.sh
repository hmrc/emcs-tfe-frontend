#!/bin/bash

sbt "run 8310 -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"