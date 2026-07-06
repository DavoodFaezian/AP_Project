import 'package:flutter/material.dart';
import 'package:test_app/custom_drawer.dart';
import 'custom_appbar.dart';
import 'package:test_app/empty_screen.dart';

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

                ? const EmptyState(
                    imagePath: "assets/images/File searching-rafiki (1).png",
                    title: "Search photos",
                    subtitle: "Search by file name, date or description.",
                  )

                : searchResult.isEmpty

                    ? const EmptyState(
                        imagePath: "assets/images/File searching-rafiki (1).png",
                        title: "No photos found",
                        subtitle: "Try another keyword.",
                      )

                    : _buildResultGrid(),
          ),        
        ],
      ),
    );
  }
}