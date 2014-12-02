#! /bin/bash

curl -H 'Content-Type: text/xml' -d '<methodCall><methodName>grav.addresses</methodName><params><param><value><struct><member><name>password</name><value><string>l</string></value></member></struct></value></param></params></methodCall>' https://secure.gravatar.com/xmlrpc?user=2d2ab5c40ea7b5ed9c532f45026d7f2f