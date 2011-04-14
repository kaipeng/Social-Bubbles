package com.social.bubbles.fsquery;

	public class FoursquareTodoResponse extends FoursquareObject{
		private FoursquareTodoList todos;
	

		public FoursquareTodo[] getTodos() {
			return todos.items;
		}
		
	}