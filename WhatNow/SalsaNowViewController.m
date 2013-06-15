//
//  SalsaNowViewController.m
//  WhatNow
//
//  Created by Victa Phu on 15/06/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "SalsaNowViewController.h"

#import <GoogleMaps/GoogleMaps.h>

@implementation SalsaNowViewController {
    GMSMapView *mapView_;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    responseData = [NSMutableData data];
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:@"http://connectwf.appspot.com/events.json"]];
    [[NSURLConnection alloc] initWithRequest:request delegate:self];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}

- (void)loadView {
    // Create a GMSCameraPosition that tells the map to display the
    // coordinate -33.86,151.20 at zoom level 6.
    GMSCameraPosition *camera = [GMSCameraPosition cameraWithLatitude:-33.86
                                                            longitude:151.20
                                                                 zoom:6];
    mapView_ = [GMSMapView mapWithFrame:CGRectZero camera:camera];
    mapView_.myLocationEnabled = YES;
    self.view = mapView_;
    
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[responseData appendData:data];
    
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error happened :O" message:@"An Error Occurred, zomg" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:nil];
    // optional - add more buttons:
    [alert addButtonWithTitle:@"Yes"];
    [alert show];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    NSError *err = nil;
    
    NSArray *jsonArray = [NSJSONSerialization JSONObjectWithData: responseData options:NSJSONReadingMutableContainers error:&err];
    
    if (!jsonArray){
        NSLog(@"Error parsing JSONL %@", err);
    }
    else{
        for(NSDictionary *item in jsonArray)
        {
            GMSMarker *marker = [[GMSMarker alloc] init];
            marker.position = CLLocationCoordinate2DMake([[item objectForKey:@"latitude"] doubleValue], [[item objectForKey:@"longtitude"] doubleValue]);
            marker.title = [item objectForKey:@"event_name"];
            marker.snippet = @"Australia";
            marker.map = mapView_;
        }
    }
}

@end
