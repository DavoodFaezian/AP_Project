import 'package:flutter/material.dart';
import 'package:test_app/custom_drawer.dart';
import 'custom_appbar.dart';

class SearchScreen extends StatefulWidget {

  const SearchScreen({super.key});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {

  bool hasSearched = false;

  /// متن وارد شده توسط کاربر
  final TextEditingController searchController =
      TextEditingController();

  /// نتایج جستجو
  final List<int> searchResult = [];

  Widget _buildInitialState() {
      return  Center(
       child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [

          Image.asset(
              'assets/images/File searching-rafiki (1).png',
              width:  220,
              height: 220,
          ),

          const SizedBox(height: 20),

          const Text(
            "Search photos",
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ), 
          ),
        ],
       )
      );
    }

    Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [

          Image.asset(
              'assets/images/File searching-rafiki (1).png',
              width:  220,
              height: 220,
          ),

          const SizedBox(height: 20),

          const Text(
            "No photos found",
            style: TextStyle(fontSize: 18),
          ),

          const SizedBox(height: 8),

          const Text(
            "Try another keyword",
            style: TextStyle(color: Colors.grey),
          ),
        ],
      ),
    );
  }

  Widget _buildResultGrid() {
    return GridView.builder(
      padding: const EdgeInsets.all(8),
      itemCount: searchResult.length,
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        crossAxisSpacing: 10,
        mainAxisSpacing: 10,
        childAspectRatio: 0.9,
      ),
      itemBuilder: (context, index) {
        return Card(
          elevation: 3,
          child: Container(
            color: Colors.grey.shade300,
            child: const Center(
              child: Icon(Icons.image, size: 60),
            ),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(

      drawer: CustomDrawer(),

      appBar: const CustomAppBar(
        title: "Search",
      ),

      body: Column(

        children: [

          //------------------------------------
          // Search Box
          //------------------------------------

          Padding(
            padding: const EdgeInsets.all(12),

            child: TextField(

              controller: searchController,

              decoration: InputDecoration(

                hintText: "Search images...",

                prefixIcon: const Icon(Icons.search),

                suffixIcon: IconButton(
                  onPressed: () {
                    searchController.clear();

                    // TODO:
                    // Clear search result
                  },

                  icon: const Icon(Icons.clear),
                ),

                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(30),
                ),

              ),

              onChanged: (value) {
                setState(() {
                  hasSearched = true;

                  searchResult.clear();
                });
              },

            ),
          ),

          //------------------------------------
          // Search Result
          //------------------------------------

          Expanded(
            child: !hasSearched
                ? _buildInitialState()
                : searchResult.isEmpty
                    ? _buildEmptyState()
                    : _buildResultGrid(),
          ),
        ],
      ),
    );
  }
}